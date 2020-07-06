package com.hw.aggregate.product;

import com.hw.aggregate.catalog.CatalogApplicationService;
import com.hw.aggregate.product.command.*;
import com.hw.aggregate.product.model.OptionItem;
import com.hw.aggregate.product.model.ProductDetail;
import com.hw.aggregate.product.model.ProductOption;
import com.hw.aggregate.product.model.ProductSku;
import com.hw.aggregate.product.representation.*;
import com.hw.shared.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class ProductApplicationService {

    @Autowired
    private ProductDetailRepo repo;

    @Autowired
    private ProductServiceLambda productServiceLambda;

    @Autowired
    private CatalogApplicationService catalogApplicationService;

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public ProductAdminGetAllPaginatedSummaryRepresentation getAllForAdmin(Integer pageNumber, Integer pageSize) {
        Sort orders = new Sort(Sort.Direction.ASC, "id");
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, orders);
        Page<ProductDetail> all = repo.findAll(pageRequest);
        return new ProductAdminGetAllPaginatedSummaryRepresentation(all.getContent(), all.getTotalPages(), all.getTotalElements());
    }

    @Transactional(readOnly = true)
    public ProductCustomerSearchByNameSummaryPaginatedRepresentation searchProductByNameForCustomer(String key, Integer pageNumber, Integer pageSize) {
        Sort orders = new Sort(Sort.Direction.ASC, "id");
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, orders);
        Page<ProductDetail> pd = repo.searchProductByNameForCustomer(key, pageRequest);
        return new ProductCustomerSearchByNameSummaryPaginatedRepresentation(pd.getContent(), pd.getTotalPages(), pd.getTotalElements());
    }

    @Transactional(readOnly = true)
    public ProductCustomerSearchByAttributesSummaryPaginatedRepresentation searchByAttributesForCustomer(String attributes, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        return new ProductCustomerSearchByAttributesSummaryPaginatedRepresentation(
                searchByAttributesDynamic(attributes, pageNumber, pageSize, true, null), null, null);
    }

    /**
     * product option can be optional or mandatory,review compare logic
     *
     * @param commands
     * @return
     */
    @Transactional(readOnly = true)
    public ProductValidationResultRepresentation validateProduct(List<ProductValidationCommand> commands) {
        boolean containInvalidValue;
        if (commands.stream().anyMatch(command -> {
            Optional<ProductDetail> byId = repo.findById(Long.parseLong(command.getProductId()));
            //validate product match
            if (byId.isEmpty())
                return true;
            List<ProductSku> collect = byId.get().getProductSkuList().stream().filter(productSku -> new TreeSet(productSku.getAttributesSales()).equals(new TreeSet(command.getAttributesSales()))).collect(Collectors.toList());
            BigDecimal skuPrice = collect.get(0).getPrice();
            //if no option present then compare final price
            if (command.getSelectedOptions() == null || command.getSelectedOptions().size() == 0) {
                return skuPrice.compareTo(command.getFinalPrice()) != 0;
            }
            //validate product option match
            List<ProductOption> storedOption = byId.get().getSelectedOptions();
            if (storedOption == null || storedOption.size() == 0)
                return true;
            boolean optionAllMatch = command.getSelectedOptions().stream().allMatch(userSelected -> {
                //check selected option is valid option
                Optional<ProductOption> first = storedOption.stream().filter(storedOptionItem -> {
                    // compare title
                    if (!storedOptionItem.title.equals(userSelected.title))
                        return false;
                    //compare option value for each title
                    String optionValue = userSelected.getOptions().get(0).getOptionValue();
                    Optional<OptionItem> first1 = storedOptionItem.options.stream().filter(optionItem -> optionItem.getOptionValue().equals(optionValue)).findFirst();
                    if (first1.isEmpty())
                        return false;
                    return true;
                }).findFirst();
                if (first.isEmpty())
                    return false;
                else {
                    return true;
                }
            });
            if (!optionAllMatch)
                return true;
            //validate product final price
            BigDecimal finalPrice = command.getFinalPrice();
            // get all price variable
            List<String> userSelectedAddOnTitles = command.getSelectedOptions().stream().map(ProductOption::getTitle).collect(Collectors.toList());
            // filter option based on title
            Stream<ProductOption> storedAddonMatchingUserSelection = byId.get().getSelectedOptions().stream().filter(var1 -> userSelectedAddOnTitles.contains(var1.getTitle()));
            // map to value detail for each title
            List<String> priceVarCollection = storedAddonMatchingUserSelection.map(storedMatchAddon -> {
                String title = storedMatchAddon.getTitle();
                //find right option for title
                Optional<ProductOption> user_addon_option = command.getSelectedOptions().stream().filter(e -> e.getTitle().equals(title)).findFirst();
                OptionItem user_optionItem = user_addon_option.get().getOptions().get(0);
                Optional<OptionItem> first = storedMatchAddon.getOptions().stream().filter(db_optionItem -> db_optionItem.getOptionValue().equals(user_optionItem.getOptionValue())).findFirst();
                return first.get().getPriceVar();
            }).collect(Collectors.toList());
            BigDecimal calc = new BigDecimal(0);
            for (String priceVar : priceVarCollection) {
                if (priceVar.contains("+")) {
                    double v = Double.parseDouble(priceVar.replace("+", ""));
                    BigDecimal bigDecimal = BigDecimal.valueOf(v);
                    calc = calc.add(bigDecimal);
                } else if (priceVar.contains("-")) {
                    double v = Double.parseDouble(priceVar.replace("-", ""));
                    BigDecimal bigDecimal = BigDecimal.valueOf(v);
                    calc = calc.subtract(bigDecimal);

                } else if (priceVar.contains("*")) {
                    double v = Double.parseDouble(priceVar.replace("*", ""));
                    BigDecimal bigDecimal = BigDecimal.valueOf(v);
                    calc = calc.multiply(bigDecimal);
                } else {
                    log.error("unknown operation type");
                }
            }
            if (calc.add(skuPrice).compareTo(finalPrice) == 0) {
                log.error("value does match for product {}, expected {} actual {}", command.getProductId(), calc.add(skuPrice), finalPrice);
                return false;
            }
            return true;
        })) containInvalidValue = true;
        else containInvalidValue = false;
        return new ProductValidationResultRepresentation(containInvalidValue);
    }

    @Transactional(readOnly = true)
    public ProductDetailCustomRepresentation getProductByIdForCustomer(Long productDetailId) {
        ProductDetail apply = productServiceLambda.getByIdForAdmin.apply(productDetailId);
        return new ProductDetailCustomRepresentation(productServiceLambda.getByIdForCustomer.apply(productDetailId));
    }

    @Transactional(readOnly = true)
    public ProductDetailAdminRepresentation getProductByIdForAdmin(Long productDetailId) {
        return new ProductDetailAdminRepresentation(productServiceLambda.getByIdForAdmin.apply(productDetailId));
    }

    @Transactional
    public ProductCreatedRepresentation createProduct(CreateProductAdminCommand command) {
        ProductDetail pd = ProductDetail.create(idGenerator.getId(), command, repo);
        return new ProductCreatedRepresentation(pd);
    }

    @Transactional
    public void updateProduct(Long id, UpdateProductAdminCommand command) {
        ProductDetail read = ProductDetail.read(id, repo);
        read.update(command, this);
    }

    @Transactional
    public void delete(Long id) {
        ProductDetail.delete(id, repo);
    }

    @Transactional
    public void decreaseActualStorageForMappedProducts(DecreaseActualStorageCommand command) {
        productServiceLambda.decreaseActualStorageForMappedProducts.accept(command);
    }

    @Transactional
    public void decreaseActualStorageForMappedProductsAdmin(DecreaseActualStorageCommand command) {
        productServiceLambda.adminDecreaseActualStorageForMappedProducts.accept(command);
    }

    @Transactional
    public void decreaseOrderStorageForMappedProducts(DecreaseOrderStorageCommand command) {
        productServiceLambda.decreaseOrderStorageForMappedProducts.accept(command);
    }

    @Transactional
    public void increaseOrderStorageForMappedProducts(IncreaseOrderStorageCommand command) {
        productServiceLambda.increaseOrderStorageForMappedProducts.accept(command);
    }

    @Transactional
    public void increaseActualStorageForMappedProducts(IncreaseActualStorageCommand command) {
        productServiceLambda.increaseActualStorageForMappedProducts.accept(command);
    }

    @Transactional
    public void increaseActualStorageForMappedProductsAdmin(IncreaseActualStorageCommand command) {
        productServiceLambda.adminIncreaseActualStorageForMappedProducts.accept(command);
    }

    @Transactional
    public void rollbackTx(String txId) {
        log.info("start of rollback transaction {}", txId);
        productServiceLambda.rollbackTx.accept(txId);
    }

    public ProductAdminSearchByAttributesSummaryPaginatedRepresentation searchByAttributesForAdmin(String tags, Integer pageNumber, Integer pageSize) {
        return new ProductAdminSearchByAttributesSummaryPaginatedRepresentation(searchByAttributesDynamic(tags, pageNumber, pageSize, false, false), null, null);
    }

    private List<ProductDetail> searchByAttributesDynamic(String attributes, Integer pageNumber, Integer pageSize, boolean customerSearch, Boolean fullSearch) {
        if ("".equals(attributes) || attributes == null) {
            return new ArrayList<>(0);
        }
        List<Object[]> resultList = entityManager.createNativeQuery("SELECT id, name, attr_key, image_url_small" +
                " FROM product_detail pd WHERE " + getWhereClause(attributes, customerSearch, fullSearch) + (customerSearch ? "AND status='AVAILABLE'" : "") + " ORDER BY id ASC LIMIT ?1, ?2")
                .setParameter(1, pageNumber * pageSize)
                .setParameter(2, pageSize)
                .getResultList();
        List<ProductDetail> productDetails = new ArrayList<>(resultList.size());
        for (Object[] row : resultList) {
            productDetails.add(new ProductDetail(((BigInteger) row[0]).longValue(), (String) row[1], (String) row[2], (String) row[3]));
        }
        productDetails.forEach(pd -> {
            List<Object[]> resultList1 = entityManager.createNativeQuery("SELECT attributes_sales, storage_order, storage_actual, price, sales" +
                    " FROM product_sku_map pd WHERE pd.product_id = ?1")
                    .setParameter(1, pd.getId())
                    .getResultList();
            ArrayList<ProductSku> productSkus = new ArrayList<>();
            for (Object[] row : resultList1) {
                productSkus.add(new ProductSku(row[0], row[1], row[2], row[3], row[4]));
            }
            pd.setProductSkuList(productSkus);
        });
        return productDetails;
    }

    private String getWhereClause(String attributes, boolean customerSearch, Boolean fullSearch) {
        //sort before search
        Set<String> strings = new TreeSet<>(Arrays.asList(attributes.split(",")));
        List<String> collect;
        if (customerSearch) {
            collect = getWhereClauseKeyAndProdAndGen(strings);
        } else {
            if (Boolean.TRUE.equals(fullSearch)) {
                collect = getWhereClauseKeyAndProdAndGen(strings);
            } else {
                collect = getWhereClauseKey(strings);
            }
        }
        return String.join(" AND ", collect);
    }


    private List<String> getWhereClauseKey(Set<String> strings) {
        return strings.stream().map(e -> "pd.attr_key LIKE '%" + e + "%'").collect(Collectors.toList());
    }

    private List<String> getWhereClauseKeyAndProd(Set<String> strings) {
        return strings.stream().map(e -> "( pd.attr_key LIKE '%" + e + "%' OR pd.attr_prod LIKE '%" + e + "%' )").collect(Collectors.toList());
    }

    private List<String> getWhereClauseKeyAndProdAndGen(Set<String> strings) {
        return strings.stream().map(e -> "( pd.attr_key LIKE '%" + e + "%' OR pd.attr_prod LIKE '%" + e + "%' OR pd.attr_gen LIKE '%" + e + "%' )").collect(Collectors.toList());
    }
}


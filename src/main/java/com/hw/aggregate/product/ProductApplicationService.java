package com.hw.aggregate.product;

import com.hw.aggregate.attribute.BizAttributeApplicationService;
import com.hw.aggregate.attribute.representation.BizAttributeSummaryRepresentation;
import com.hw.aggregate.catalog.CatalogApplicationService;
import com.hw.aggregate.product.command.*;
import com.hw.aggregate.product.model.*;
import com.hw.aggregate.product.representation.*;
import com.hw.shared.IdGenerator;
import com.hw.shared.SortOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.time.Instant;
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
    private BizAttributeApplicationService attributeApplicationService;

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public ProductAdminGetAllPaginatedSummaryRepresentation getAllForAdmin(Integer pageNumber, Integer pageSize, ProductDetail.AdminSortConfig sortBy, SortOrder sortOrder) {
        PageRequest of = ProductDetail.AdminSortConfig.getPageRequestAdmin(pageNumber, pageSize, sortBy, sortOrder);
        Page<ProductDetail> all = repo.findAll(of);
        return new ProductAdminGetAllPaginatedSummaryRepresentation(all.getContent(), all.getTotalPages(), all.getTotalElements());
    }


    @Transactional(readOnly = true)
    public ProductCustomerSearchByNameSummaryPaginatedRepresentation searchProductByNameForCustomer(String key, Integer pageNumber, Integer pageSize, ProductDetail.CustomerSortConfig sortBy, SortOrder sortOrder) {
        PageRequest pageRequest = ProductDetail.CustomerSortConfig.getPageRequestCustomer(pageNumber, pageSize, sortBy, sortOrder);
        Page<ProductDetail> pd = repo.searchProductByNameForCustomer(key, Instant.now().toEpochMilli(), pageRequest);
        return new ProductCustomerSearchByNameSummaryPaginatedRepresentation(pd.getContent(), pd.getTotalPages(), pd.getTotalElements());
    }


    @Transactional(readOnly = true)
    public ProductCustomerSearchByAttributesSummaryPaginatedRepresentation searchByAttributesForCustomer(String attributes, Integer pageNumber, Integer pageSize, ProductDetail.CustomerSortConfig sortBy, SortOrder sortOrder) {
        PageRequest of = ProductDetail.CustomerSortConfig.getPageRequestCustomer(pageNumber, pageSize, sortBy, sortOrder);
        return new ProductCustomerSearchByAttributesSummaryPaginatedRepresentation(
                searchByAttributesDynamicNew(attributes, true, of), null, null);
    }

    @Transactional(readOnly = true)
    public ProductAdminSearchByAttributesSummaryPaginatedRepresentation searchByAttributesForAdmin(String tags, Integer pageNumber, Integer pageSize, ProductDetail.AdminSortConfig sortBy, SortOrder sortOrder) {
        PageRequest of = ProductDetail.AdminSortConfig.getPageRequestAdmin(pageNumber, pageSize, sortBy, sortOrder);
        return new ProductAdminSearchByAttributesSummaryPaginatedRepresentation(searchByAttributesDynamicNew(tags, false, of), null, null);
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
            if (byId.isEmpty() || !ProductDetail.isAvailable(byId.get()))
                return true;
            BigDecimal price;
            if (byId.get().getProductSkuList() != null && byId.get().getProductSkuList().size() != 0) {
                List<ProductSku> collect = byId.get().getProductSkuList().stream().filter(productSku -> new TreeSet(productSku.getAttributesSales()).equals(new TreeSet(command.getAttributesSales()))).collect(Collectors.toList());
                price = collect.get(0).getPrice();
            } else {
                price = byId.get().getLowestPrice();
            }
            //if no option present then compare final price
            if (command.getSelectedOptions() == null || command.getSelectedOptions().size() == 0) {
                return price.compareTo(command.getFinalPrice()) != 0;
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
            if (calc.add(price).compareTo(finalPrice) == 0) {
                log.error("value does match for product {}, expected {} actual {}", command.getProductId(), calc.add(price), finalPrice);
                return false;
            }
            return true;
        })) containInvalidValue = true;
        else containInvalidValue = false;
        return new ProductValidationResultRepresentation(containInvalidValue);
    }

    @Transactional(readOnly = true)
    public ProductDetailCustomRepresentation getProductByIdForCustomer(Long productDetailId) {
        ProductDetail productDetail = ProductDetail.readCustomer(productDetailId, repo);
        BizAttributeSummaryRepresentation allAttributes = attributeApplicationService.getAllAttributes();
        return new ProductDetailCustomRepresentation(productDetail, allAttributes);
    }

    @Transactional(readOnly = true)
    public ProductDetailAdminRepresentation getProductByIdForAdmin(Long productDetailId) {
        return new ProductDetailAdminRepresentation(ProductDetail.readAdmin(productDetailId, repo));
    }

    @Transactional
    public ProductCreatedRepresentation createProduct(CreateProductAdminCommand command) {
        ProductDetail pd = ProductDetail.create(idGenerator.getId(), command, repo);
        return new ProductCreatedRepresentation(pd);
    }

    @Transactional
    public void updateProduct(Long id, UpdateProductAdminCommand command) {
        ProductDetail read = ProductDetail.readAdmin(id, repo);
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

    @Transactional
    public void updateProductStatus(Long id, ProductStatus status) {
        ProductDetail read = ProductDetail.readAdmin(id, repo);
        read.updateStatus(status, repo);
    }

    private List<ProductDetail> searchByAttributesDynamicNew(String attributes, boolean customerSearch, PageRequest pageRequest) {
        if ("".equals(attributes) || attributes == null) {
            return new ArrayList<>(0);
        }
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProductDetail> query = cb.createQuery(ProductDetail.class);
        Root<ProductDetail> root = query.from(ProductDetail.class);
        query.select(root);
        Predicate attrClause = getAttrWhereClause(attributes, cb, root);
        if (customerSearch) {
            Predicate statusClause = getStatusClause(cb, root);
            Predicate and = cb.and(attrClause, statusClause);
            query.where(and);
        } else {
            query.where(attrClause);
        }
        TypedQuery<ProductDetail> query1 = entityManager.createQuery(query);
        return query1.getResultList();
    }

    private Predicate getStatusClause(CriteriaBuilder cb, Root<ProductDetail> root) {
        Predicate startAtLessThanOrEqualToCurrentEpochMilli = cb.lessThanOrEqualTo(root.get("startAt").as(Long.class), Instant.now().toEpochMilli());
        Predicate startAtNotNull = cb.isNotNull(root.get("startAt").as(Long.class));
        Predicate and = cb.and(startAtNotNull, startAtLessThanOrEqualToCurrentEpochMilli);
        Predicate endAtGreaterThanCurrentEpochMilli = cb.gt(root.get("endAt").as(Long.class), Instant.now().toEpochMilli());
        Predicate endAtIsNull = cb.isNull(root.get("endAt").as(Long.class));
        Predicate or = cb.or(endAtGreaterThanCurrentEpochMilli, endAtIsNull);
        return cb.and(and, or);
    }

    private Predicate getAttrWhereClause(String attributes, CriteriaBuilder cb, Root<ProductDetail> root) {
        //sort before search
        Set<String> strings = new TreeSet<>(Arrays.asList(attributes.split(",")));
        List<Predicate> list1 = strings.stream().filter(e -> !e.contains("$")).map(e -> getAndExpression(e, cb, root)).collect(Collectors.toList());
        List<Predicate> list2 = strings.stream().filter(e -> e.contains("$")).map(e -> getOrExpression(e, cb, root)).collect(Collectors.toList());
        list1.addAll(list2);
        return cb.and(list1.toArray(new Predicate[0]));
    }

    private Predicate getOrExpression(String input, CriteriaBuilder cb, Root<ProductDetail> root) {
        String name = input.split(":")[0];
        String[] values = input.split(":")[1].split("\\$");
        Set<String> collect = Arrays.stream(values).map(el -> name + ":" + el).collect(Collectors.toSet());

        String[] strs = {"attrKey", "attrProd", "attrGen", "attrSalesTotal"};
        Predicate[] predicates = Arrays.stream(strs)
                .map(ee -> collect.stream().map(e -> cb.like(root.get(ee).as(String.class), "%" + e + "%")).collect(Collectors.toSet()))
                .flatMap(Collection::stream).distinct().toArray(Predicate[]::new);
        return cb.or(predicates);
    }

    private Predicate getAndExpression(String input, CriteriaBuilder cb, Root<ProductDetail> root) {
        String[] strs = {"attrKey", "attrProd", "attrGen", "attrSalesTotal"};
        Predicate[] predicates = Arrays.stream(strs)
                .map(ee -> cb.like(root.get(ee).as(String.class), "%" + input + "%"))
                .collect(Collectors.toSet()).toArray(Predicate[]::new);
        return cb.or(predicates);
    }

}


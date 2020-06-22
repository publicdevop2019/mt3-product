package com.hw.aggregate.product;

import com.hw.aggregate.catalog.CatalogApplicationService;
import com.hw.aggregate.product.command.*;
import com.hw.aggregate.product.model.*;
import com.hw.aggregate.product.representation.*;
import com.hw.shared.BadRequestException;
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
    private ProductDetailRepo productDetailRepo;

    @Autowired
    private ProductServiceLambda productServiceLambda;

    @Autowired
    private CatalogApplicationService catalogApplicationService;

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public ProductSearchAdminPaginatedSummaryRepresentation getAllForAdmin(Integer pageNumber, Integer pageSize) {
        Sort orders = new Sort(Sort.Direction.ASC, "id");
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, orders);
        Page<ProductDetail> all = productDetailRepo.findAll(pageRequest);
        return new ProductSearchAdminPaginatedSummaryRepresentation(all.getContent(), all.getTotalPages(), all.getTotalElements());
    }

    @Transactional(readOnly = true)
    public ProductSearchResultRepresentation searchProductForCustomer(String key, Integer pageNumber, Integer pageSize) {
        Sort orders = new Sort(Sort.Direction.ASC, "id");
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, orders);
        return new ProductSearchResultRepresentation(productDetailRepo.searchProductByName(key, pageRequest));
    }

    @Transactional(readOnly = true)
    public ProductSearchTagsCustomerSummaryRepresentation searchByTagsForCustomer(String tags, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort initialSort = new Sort(Sort.Direction.ASC, SortCriteriaEnum.fromString(sortBy).getSortCriteria());
        Sort finalSort;
        if (sortOrder.equalsIgnoreCase(SortOrderEnum.ASC.getSortOrder())) {
            finalSort = initialSort.ascending();
        } else if (sortOrder.equalsIgnoreCase(SortOrderEnum.DESC.getSortOrder())) {
            finalSort = initialSort.descending();
        } else {
            throw new BadRequestException("unsupported sort order");
        }
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, finalSort);
        HashSet<String> strings = new HashSet<>(Arrays.asList(tags.split(",")));
        return new ProductSearchTagsCustomerSummaryRepresentation(productDetailRepo.findProductByTags(strings, pageRequest).getContent());
    }

    /**
     * product option can be optional or mandatory,review compare logic
     *
     * @param products
     * @return
     */
    @Transactional(readOnly = true)
    public ProductValidationResultRepresentation validateProduct(List<ProductValidationCommand> products) {
        boolean containInvalidValue;
        if (products.stream().anyMatch(user_product -> {
            Optional<ProductDetail> byId = productDetailRepo.findById(Long.parseLong(user_product.getProductId()));
            /**
             * validate product match
             */
            if (byId.isEmpty())
                return true;
            /**
             * if no option present then compare final price
             */
            if (user_product.getSelectedOptions() == null || user_product.getSelectedOptions().size() == 0) {
                return BigDecimal.valueOf(Double.parseDouble(user_product.getFinalPrice())).compareTo(byId.get().getPrice()) != 0;
            }
            /**
             * validate product option match
             */
            List<ProductOption> storedOption = byId.get().getSelectedOptions();
            if (storedOption == null || storedOption.size() == 0)
                return true;
            boolean optionAllMatch = user_product.getSelectedOptions().stream().allMatch(userSelected -> {
                /** check selected option is valid option */
                Optional<ProductOption> first = storedOption.stream().filter(storedOptionItem -> {
                    /** compare title */
                    if (!storedOptionItem.title.equals(userSelected.title))
                        return false;
                    /**compare option value for each title*/
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
            /**
             * validate product final price
             */
            String finalPrice = user_product.getFinalPrice();
            /** get all price variable */
            List<String> userSelectedAddOnTitles = user_product.getSelectedOptions().stream().map(ProductOption::getTitle).collect(Collectors.toList());
            /** filter option based on title */
            Stream<ProductOption> storedAddonMacthingUserSelection = byId.get().getSelectedOptions().stream().filter(var1 -> userSelectedAddOnTitles.contains(var1.getTitle()));
            /** map to value detail for each title */
            List<String> priceVarCollection = storedAddonMacthingUserSelection.map(storedMatchAddon -> {
                String title = storedMatchAddon.getTitle();
                /**
                 * find right option for title
                 */
                Optional<ProductOption> user_addon_option = user_product.getSelectedOptions().stream().filter(e -> e.getTitle().equals(title)).findFirst();
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
            if (calc.add(byId.get().getPrice()).compareTo(BigDecimal.valueOf(Double.parseDouble(finalPrice))) == 0) {
                log.error("value does match for product {}, expected {} actual {}", user_product.getProductId(), calc.add(byId.get().getPrice()), BigDecimal.valueOf(Double.parseDouble(finalPrice)));
                return false;
            }
            return true;
        })) containInvalidValue = true;
        else containInvalidValue = false;
        return new ProductValidationResultRepresentation(containInvalidValue);
    }

    @Transactional(readOnly = true)
    public ProductDetailCustomRepresentation getProductByIdForCustomer(Long productDetailId) {
        return new ProductDetailCustomRepresentation(productServiceLambda.getById.apply(productDetailId));
    }

    @Transactional(readOnly = true)
    public ProductDetailAdminRepresentation getProductByIdForAdmin(Long productDetailId) {
        return new ProductDetailAdminRepresentation(productServiceLambda.getById.apply(productDetailId));
    }

    @Transactional
    public ProductCreatedRepresentation createProduct(CreateProductAdminCommand productDetail) {
        ProductDetail productDetail1 = ProductDetail.create(
                idGenerator.getId(),
                productDetail.getImageUrlSmall(), productDetail.getName(), productDetail.getOrderStorage()
                , productDetail.getActualStorage(), productDetail.getDescription(),
                productDetail.getRate(), productDetail.getPrice(), productDetail.getSales(),
                productDetail.getCatalog(), productDetail.getSelectedOptions(),
                productDetail.getImageUrlLarge(), productDetail.getSpecification());
        return new ProductCreatedRepresentation(productDetailRepo.save(productDetail1).getId().toString());
    }

    @Transactional
    public void updateProduct(Long productDetailId, UpdateProductAdminCommand newProductDetail) {
        productServiceLambda.getById.andThen(productServiceLambda.update).accept(productDetailId, newProductDetail);
    }

    @Transactional
    public void delete(DeleteProductAdminCommand command) {
        productDetailRepo.deleteById(command.getId());
    }

    @Transactional
    public void decreaseActualStorageForMappedProducts(DecreaseActualStorageCommand command) {
        productServiceLambda.decreaseActualStorageForMappedProducts.accept(command.getProductMap(), command.getOptToken());
    }

    @Transactional
    public void decreaseOrderStorageForMappedProducts(DecreaseOrderStorageCommand command) {
        productServiceLambda.decreaseOrderStorageForMappedProducts.accept(command.getProductMap(), command.getOptToken());
    }

    @Transactional
    public void increaseOrderStorageForMappedProducts(IncreaseOrderStorageCommand command) {
        productServiceLambda.increaseOrderStorageForMappedProducts.accept(command.getProductMap(), command.getOptToken());
    }

    @Transactional
    public void revoke(RevokeRecordedChangeCommand command) {
        log.info("start of revoke transaction {}", command.getOptToken());
        productServiceLambda.revoke.accept(command.getOptToken());
    }

    public ProductSearchAdminPaginatedSummaryRepresentation searchByTagsForAdmin(String tags, Integer pageNumber, Integer pageSize) {
        List<Object[]> resultList = entityManager.createNativeQuery("SELECT id, name, price, sales,tags, order_storage, actual_storage" +
                " FROM product_detail pd WHERE " + getWhereClause(tags) + " ORDER BY price ASC LIMIT ?1, ?2")
                .setParameter(1, pageNumber * pageSize)
                .setParameter(2, pageSize)
                .getResultList();
        List<ProductDetail> productDetails = new ArrayList<>(resultList.size());
        for (Object[] row : resultList) {
            productDetails.add(new ProductDetail(((BigInteger) row[0]).longValue(), (String) row[1], (BigDecimal) row[2],
                    (Integer) row[3], (String) row[4], (Integer) row[5], (Integer) row[6]));
        }
        return new ProductSearchAdminPaginatedSummaryRepresentation(productDetails);
    }

    private String getWhereClause(String tags) {
        HashSet<String> hashSet = new HashSet<>(Arrays.asList(tags.split(",")));
        List<String> collect = hashSet.stream().map(tag -> "pd.tags LIKE '%" + tag + "%'").collect(Collectors.toList());
        return String.join(" AND ", collect);
    }
}


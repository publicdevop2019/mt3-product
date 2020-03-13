package com.hw.service;

import com.hw.clazz.*;
import com.hw.entity.ProductDetail;
import com.hw.entity.ProductSimple;
import com.hw.entity.SnapshotProduct;
import com.hw.repo.ProductDetailRepo;
import com.hw.shared.*;
import com.hw.vo.ProductTotalResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class ProductService {

    @Autowired
    private ProductDetailRepo productDetailRepo;

    @Autowired
    private CategoryService categoryService;

    public ProductTotalResponse getAll(Integer pageNumber, Integer pageSize) {
        Sort orders = new Sort(Sort.Direction.ASC, "id");
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, orders);
        Page<ProductDetail> all = productDetailRepo.findAll(pageRequest);
        return new ProductTotalResponse(extractProductSimple(Optional.of(all.getContent())), all.getTotalPages(), all.getTotalElements());
    }

    public List<ProductSimple> search(String key, Integer pageNumber, Integer pageSize) {
        Sort orders = new Sort(Sort.Direction.ASC, "id");
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, orders);
        return extractProductSimple(productDetailRepo.searchProductByName(key, pageRequest));
    }

    public synchronized String create(ProductDetail productDetail) {
        return productDetailRepo.save(productDetail).getId().toString();
    }

    public ThrowingBiConsumer<ProductDetail, ProductDetail, ProductException> update = (old, next) -> {
        if (next.getOrderStorage() != null || next.getActualStorage() != null)
            throw new ProductException("use increaseBy or decreaseBy to update storage value");
        Integer orderStorageCopied = old.getOrderStorage();
        Integer actualStorageCopied = old.getActualStorage();
        BeanUtils.copyProperties(next, old);
        old.setOrderStorage(orderStorageCopied);
        old.setActualStorage(actualStorageCopied);
        if (next.getIncreaseOrderStorageBy() != null)
            old.setOrderStorage(old.getOrderStorage() + next.getIncreaseOrderStorageBy());
        if (next.getDecreaseOrderStorageBy() != null)
            old.setOrderStorage(old.getOrderStorage() - (next.getDecreaseOrderStorageBy()));

        if (next.getIncreaseActualStorageBy() != null)
            old.setActualStorage(old.getActualStorage() + next.getIncreaseActualStorageBy());
        if (next.getDecreaseActualStorageBy() != null)
            old.setActualStorage(old.getActualStorage() - (next.getDecreaseActualStorageBy()));
        productDetailRepo.save(old);
    };

    public List<ProductSimple> getByCategory(String categoryName, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
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
        if (categoryService.getAll().stream().noneMatch(e -> e.getTitle().equals(categoryName)))
            throw new ProductException("categoryName :: " + categoryName + " not found");
        return extractProductSimple(Optional.of(productDetailRepo.findProductByCategory(categoryName, pageRequest).getContent()));
    }

    public ThrowingFunction<Long, ProductDetail, ProductException> getById = (productDetailId) -> {
        Optional<ProductDetail> findById = productDetailRepo.findById(productDetailId);
        if (findById.isEmpty())
            throw new ProductException("productDetailId not found :: " + productDetailId);
        return findById.get();
    };

    public void delete(Long productDetailId) {
        productDetailRepo.delete(getById.apply(productDetailId));
    }

    private BiConsumer<ProductDetail, Integer> increaseOrderStorage = (productDetail, increaseBy) -> {
        productDetail.setOrderStorage(productDetail.getOrderStorage() + increaseBy);
        productDetailRepo.save(productDetail);
    };

    public ThrowingConsumer<Map<String, String>, ProductException> increaseOrderStorageForMappedProducts = (map) -> {
        map.keySet().forEach(productDetailId -> {
            synchronized (productDetailRepo) {
                getById.andThen(increaseOrderStorage).accept(Long.parseLong(productDetailId), Integer.parseInt(map.get(productDetailId)));
            }
        });
    };

    private ThrowingBiFunction<Integer, Integer, Integer, ProductException> calcNextStorageValue = (storage, decreaseBy) -> {
        if (0 == storage)
            throw new ProductException("product storage is empty");
        Integer output = storage - decreaseBy;
        if (output < 0)
            throw new ProductException("product storage not enough");
        return output;
    };

    private ThrowingBiConsumer<ProductDetail, Integer, ProductException> decreaseOrderStorage = (pd, decreaseBy) -> {
        Integer apply = calcNextStorageValue.apply(pd.getOrderStorage(), decreaseBy);
        log.info("after calc, new order storage value is " + apply);
        pd.setOrderStorage(apply);
        productDetailRepo.save(pd);
    };


    private ThrowingBiConsumer<ProductDetail, Integer, ProductException> decreaseActualStorage = (pd, decreaseBy) -> {
        pd.setActualStorage(calcNextStorageValue.apply(pd.getActualStorage(), decreaseBy));
        if (pd.getSales() == null) {
            pd.setSales(decreaseBy);
        } else {
            pd.setSales(pd.getSales() + decreaseBy);
        }
        productDetailRepo.save(pd);
    };


    public ThrowingConsumer<Map<String, String>, ProductException> decreaseOrderStorageForMappedProducts = (map) -> {
        map.keySet().forEach(productDetailId -> {
            synchronized (productDetailRepo) {
                getById.andThen(decreaseOrderStorage).accept(Long.parseLong(productDetailId), Integer.parseInt(map.get(productDetailId)));
            }
        });
    };

    public ThrowingConsumer<Map<String, String>, ProductException> decreaseActualStorageForMappedProducts = (map) -> {
        map.keySet().forEach(productDetailId -> {
            synchronized (productDetailRepo) {
                getById.andThen(decreaseActualStorage).accept(Long.parseLong(productDetailId), Integer.parseInt(map.get(productDetailId)));
            }
        });
    };

    private List<ProductSimple> extractProductSimple(Optional<List<ProductDetail>> productDetails) {
        List<ProductSimple> productSimpleList = new ArrayList<>();
        if (productDetails.isEmpty())
            return productSimpleList;
        productDetails.get().forEach(e -> {
            ProductSimple productSimple = new ProductSimple();
            BeanUtils.copyProperties(e, productSimple);
            productSimpleList.add(productSimple);
        });
        return productSimpleList;
    }

    public Boolean validate(List<SnapshotProduct> products) {
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
            if (user_product.getSelectedOptions() == null || user_product.getSelectedOptions().size() == 0)
                if (!user_product.getFinalPrice().equals(byId.get().getPrice().toString()))
                    return true;
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
        return containInvalidValue;
    }
}

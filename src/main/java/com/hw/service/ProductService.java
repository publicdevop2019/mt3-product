package com.hw.service;

import com.hw.clazz.OptionItem;
import com.hw.clazz.ProductOption;
import com.hw.entity.ProductDetail;
import com.hw.entity.ProductSimple;
import com.hw.entity.SnapshotProduct;
import com.hw.repo.ProductDetailRepo;
import com.hw.shared.ThrowingBiConsumer;
import com.hw.shared.ThrowingConsumer;
import com.hw.shared.ThrowingFunction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class ProductService {

    @Autowired
    ProductDetailRepo productDetailRepo;

    public List<ProductSimple> getAllProducts() {
        List<ProductDetail> productByCategory = productDetailRepo.findAll();
        return extractProductSimple(Optional.of(productByCategory));
    }

    public List<ProductSimple> searchProduct(String key) {
        Optional<List<ProductDetail>> productDetails = productDetailRepo.searchProductByName(key);
        return extractProductSimple(productDetails);
    }

    public Boolean validateProductDetails(List<SnapshotProduct> products) {
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
            if (calc.add(byId.get().getPrice()).compareTo(BigDecimal.valueOf(Double.parseDouble(finalPrice))) == 0)
                return false;
            return true;
        })) containInvalidValue = true;
        else containInvalidValue = false;
        return containInvalidValue;
    }

    public synchronized String createProduct(ProductDetail productDetail) {
        ProductDetail save = productDetailRepo.save(productDetail);
        return save.getId().toString();
    }

    public ThrowingBiConsumer<Object, Object, Exception> updateProduct = (productDetailId, newProductDetail) -> {
        synchronized (productDetailRepo) {
            Optional<ProductDetail> findById = productDetailRepo.findById((Long) productDetailId);
            ProductDetail newProductDetail1 = (ProductDetail) newProductDetail;
            if (findById.isEmpty())
                throw new Exception("productDetailId not found ::" + productDetailId);
            if (newProductDetail1.getOrderStorage() != null || newProductDetail1.getActualStorage() != null)
                throw new Exception("use increaseBy or decreaseBy to update storage value");
            ProductDetail oldProductSimple = findById.get();
            Integer orderStorageCopied = oldProductSimple.getOrderStorage();
            Integer actualStorageCopied = oldProductSimple.getActualStorage();
            BeanUtils.copyProperties(newProductDetail, oldProductSimple);
            oldProductSimple.setOrderStorage(orderStorageCopied);
            oldProductSimple.setActualStorage(actualStorageCopied);
            if (newProductDetail1.getIncreaseOrderStorageBy() != null)
                oldProductSimple.setOrderStorage(oldProductSimple.getOrderStorage() + newProductDetail1.getIncreaseOrderStorageBy());
            if (newProductDetail1.getDecreaseOrderStorageBy() != null)
                oldProductSimple.setOrderStorage(oldProductSimple.getOrderStorage() - (newProductDetail1.getDecreaseOrderStorageBy()));

            if (newProductDetail1.getIncreaseActualStorageBy() != null)
                oldProductSimple.setActualStorage(oldProductSimple.getActualStorage() + newProductDetail1.getIncreaseActualStorageBy());
            if (newProductDetail1.getDecreaseActualStorageBy() != null)
                oldProductSimple.setActualStorage(oldProductSimple.getActualStorage() - (newProductDetail1.getDecreaseActualStorageBy()));
            productDetailRepo.save(oldProductSimple);
        }
    };

    public ThrowingFunction<Object, Object, Exception> getProductsByCategory = (categoryName) -> {
        Optional<List<ProductDetail>> productByCategory = productDetailRepo.findProductByCategory((String) categoryName);
        if (productByCategory.isEmpty())
            throw new Exception("categoryName::" + categoryName + " not found");
        return extractProductSimple(productByCategory);
    };

    public ThrowingFunction<Object, Object, Exception> getProductById = (productDetailId) -> {
        Optional<ProductDetail> findById = productDetailRepo.findById((Long) productDetailId);
        if (findById.isEmpty())
            throw new Exception("productDetailId not found ::" + productDetailId);
        return findById.get();
    };

    public ThrowingConsumer<Object, Exception> deleteProduct = (productDetailId) -> {
        Optional<ProductDetail> findById = productDetailRepo.findById((Long) productDetailId);
        if (findById.isEmpty())
            throw new Exception("productDetailId not found ::" + productDetailId);
        productDetailRepo.delete(findById.get());
    };

    public ThrowingConsumer<Object, Exception> increaseOrderStorage = (map) -> {
        ((Map<String, String>) map).keySet().forEach(productDetailId -> {
            synchronized (productDetailRepo) {
                Optional<ProductDetail> findById = productDetailRepo.findById(Long.parseLong(productDetailId));
                if (findById.isEmpty())
                    throw new RuntimeException("product id::" + productDetailId + " not found");
                ProductDetail oldProductSimple = findById.get();
                oldProductSimple.setOrderStorage(oldProductSimple.getOrderStorage() + Integer.parseInt(((Map<String, String>) map).get(productDetailId)));
                productDetailRepo.save(oldProductSimple);
            }
        });
    };

    public ThrowingConsumer<Object, Exception> decreaseOrderStorage = (map) -> {
        ((Map<String, String>) map).keySet().forEach(productDetailId -> {
            synchronized (productDetailRepo) {
                Optional<ProductDetail> findById = productDetailRepo.findById(Long.parseLong(productDetailId));
                if (findById.isEmpty())
                    throw new RuntimeException("product id::" + productDetailId + " not found");
                ProductDetail oldProductSimple = findById.get();
                if (oldProductSimple.getOrderStorage() == null || 0 == oldProductSimple.getOrderStorage())
                    throw new RuntimeException("product id::" + productDetailId + " storage is empty");
                int output = oldProductSimple.getOrderStorage() - Integer.parseInt(((Map<String, String>) map).get(productDetailId));
                if (output < 0)
                    throw new RuntimeException("product id::" + productDetailId + " storage not enough");
                oldProductSimple.setOrderStorage(output);
                productDetailRepo.save(oldProductSimple);
            }
        });
    };
    public ThrowingConsumer<Object, Exception> decreaseActualStorage = (map) -> {
        ((Map<String, String>) map).keySet().forEach(productDetailId -> {
            synchronized (productDetailRepo) {
                Optional<ProductDetail> findById = productDetailRepo.findById(Long.parseLong(productDetailId));
                if (findById.isEmpty())
                    throw new RuntimeException("product id::" + productDetailId + " not found");
                ProductDetail oldProductSimple = findById.get();
                if (oldProductSimple.getActualStorage() == null || 0 == oldProductSimple.getActualStorage())
                    throw new RuntimeException("product id::" + productDetailId + " storage is empty");
                int output = oldProductSimple.getActualStorage() - Integer.parseInt(((Map<String, String>) map).get(productDetailId));
                if (output < 0)
                    throw new RuntimeException("product id::" + productDetailId + " storage not enough");
                oldProductSimple.setActualStorage(output);
                if (oldProductSimple.getSales() == null) {
                    oldProductSimple.setSales(Integer.parseInt(((Map<String, String>) map).get(productDetailId)));
                } else {
                    oldProductSimple.setSales(oldProductSimple.getSales() + Integer.parseInt(((Map<String, String>) map).get(productDetailId)));
                }
                productDetailRepo.save(oldProductSimple);
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
}

package com.hw.service;

import com.hw.clazz.OptionItem;
import com.hw.clazz.ProductOption;
import com.hw.entity.ProductDetail;
import com.hw.entity.ProductSimple;
import com.hw.entity.SnapshotProduct;
import com.hw.repo.ProductDetailRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

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

    public void decreaseOrderStorage(Map<String, String> map) throws RuntimeException {
        map.keySet().forEach(productDetailId -> {
            Optional<ProductDetail> findById = productDetailRepo.findById(Long.parseLong(productDetailId));
            if (findById.isEmpty())
                throw new RuntimeException("product id::" + productDetailId + " not found");
            ProductDetail oldProductSimple = findById.get();
            if (oldProductSimple.getOrderStorage() == null || 0 == oldProductSimple.getOrderStorage())
                throw new RuntimeException("product id::" + productDetailId + " storage is empty");
            int output = oldProductSimple.getOrderStorage() - Integer.parseInt(map.get(productDetailId));
            if (output < 0)
                throw new RuntimeException("product id::" + productDetailId + " storage not enough");
            oldProductSimple.setOrderStorage(output);
            productDetailRepo.save(oldProductSimple);
        });
    }

    public void decreaseActualStorage(Map<String, String> map) throws RuntimeException {
        map.keySet().forEach(productDetailId -> {
            Optional<ProductDetail> findById = productDetailRepo.findById(Long.parseLong(productDetailId));
            if (findById.isEmpty())
                throw new RuntimeException("product id::" + productDetailId + " not found");
            ProductDetail oldProductSimple = findById.get();
            if (oldProductSimple.getActualStorage() == null || 0 == oldProductSimple.getActualStorage())
                throw new RuntimeException("product id::" + productDetailId + " storage is empty");
            int output = oldProductSimple.getActualStorage() - Integer.parseInt(map.get(productDetailId));
            if (output < 0)
                throw new RuntimeException("product id::" + productDetailId + " storage not enough");
            oldProductSimple.setActualStorage(output);
            if (oldProductSimple.getSales() == null) {
                oldProductSimple.setSales(Integer.parseInt(map.get(productDetailId)));
            } else {
                oldProductSimple.setSales(oldProductSimple.getSales() + Integer.parseInt(map.get(productDetailId)));
            }
            productDetailRepo.save(oldProductSimple);
        });
    }

    public void increaseOrderStorage(Map<String, String> map) throws RuntimeException {
        map.keySet().forEach(productDetailId -> {
            Optional<ProductDetail> findById = productDetailRepo.findById(Long.parseLong(productDetailId));
            if (findById.isEmpty())
                throw new RuntimeException("product id::" + productDetailId + " not found");
            ProductDetail oldProductSimple = findById.get();
            oldProductSimple.setOrderStorage(oldProductSimple.getOrderStorage() + Integer.parseInt(map.get(productDetailId)));
            productDetailRepo.save(oldProductSimple);
        });
    }

    public List<ProductSimple> getProductsByCategory(String categoryName) throws Exception {
        Optional<List<ProductDetail>> productByCategory = productDetailRepo.findProductByCategory(categoryName);
        if (productByCategory.isEmpty())
            throw new Exception("categoryName::" + categoryName + " not found");
        return extractProductSimple(productByCategory);
    }

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

    public ProductDetail getProductById(Long productDetailId) throws Exception {
        Optional<ProductDetail> findById = productDetailRepo.findById(productDetailId);
        if (findById.isEmpty())
            throw new Exception("productDetailId not found ::" + productDetailId);
        return findById.get();
    }

    public String createProduct(ProductDetail productDetail) {
        ProductDetail save = productDetailRepo.save(productDetail);
        return save.getId().toString();
    }

    public void updateProduct(Long productDetailId, ProductDetail newProductDetail) throws Exception {
        Optional<ProductDetail> findById = productDetailRepo.findById(productDetailId);
        if (findById.isEmpty())
            throw new Exception("productDetailId not found ::" + productDetailId);
        if (newProductDetail.getOrderStorage() != null || newProductDetail.getActualStorage() != null)
            throw new Exception("use increaseBy or decreaseBy to update storage value");
        ProductDetail oldProductSimple = findById.get();
        Integer orderStorageCopied = oldProductSimple.getOrderStorage();
        Integer actualStorageCopied = oldProductSimple.getActualStorage();
        BeanUtils.copyProperties(newProductDetail, oldProductSimple);
        oldProductSimple.setOrderStorage(orderStorageCopied);
        oldProductSimple.setActualStorage(actualStorageCopied);
        if (newProductDetail.getIncreaseOrderStorageBy() != null)
            oldProductSimple.setOrderStorage(oldProductSimple.getOrderStorage() + newProductDetail.getIncreaseOrderStorageBy());
        if (newProductDetail.getDecreaseOrderStorageBy() != null)
            oldProductSimple.setOrderStorage(oldProductSimple.getOrderStorage() - (newProductDetail.getDecreaseOrderStorageBy()));

        if (newProductDetail.getIncreaseActualStorageBy() != null)
            oldProductSimple.setActualStorage(oldProductSimple.getActualStorage() + newProductDetail.getIncreaseActualStorageBy());
        if (newProductDetail.getDecreaseActualStorageBy() != null)
            oldProductSimple.setActualStorage(oldProductSimple.getActualStorage() - (newProductDetail.getDecreaseActualStorageBy()));
        productDetailRepo.save(oldProductSimple);
    }

    public void deleteProduct(@PathVariable(name = "productDetailId") Long productDetailId) throws Exception {
        Optional<ProductDetail> findById = productDetailRepo.findById(productDetailId);
        if (findById.isEmpty())
            throw new Exception("productDetailId not found ::" + productDetailId);
        productDetailRepo.delete(findById.get());
    }

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

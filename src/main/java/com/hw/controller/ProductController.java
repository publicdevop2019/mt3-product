package com.hw.controller;

import com.hw.clazz.OptionItem;
import com.hw.clazz.ProductOption;
import com.hw.entity.ProductDetail;
import com.hw.entity.ProductSimple;
import com.hw.entity.SnapshotProduct;
import com.hw.repo.ProductDetailRepo;
import com.hw.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RestController
@RequestMapping(path = "v1/api", produces = "application/json")
public class ProductController {

    @Autowired
    ProductDetailRepo productDetailRepo;

    @Autowired
    ProductService productService;

    /**
     * public access
     */
    @GetMapping("categories/{categoryName}")
    public ResponseEntity<?> getProductsByCategory(@PathVariable(name = "categoryName") String categoryName) {
        Optional<List<ProductDetail>> productByCategory = productDetailRepo.findProductByCategory(categoryName);
        if (productByCategory.isEmpty())
            return ResponseEntity.notFound().build();
        List<ProductSimple> productSimpleArrayList = new ArrayList<>();
        productByCategory.get().stream().forEach(e -> {
            ProductSimple productSimple = new ProductSimple();
            BeanUtils.copyProperties(e, productSimple);
            productSimpleArrayList.add(productSimple);
        });
        return ResponseEntity.ok(productSimpleArrayList);
    }

    @GetMapping("categories/all")
    public ResponseEntity<?> getAllProducts() {
        List<ProductDetail> productByCategory = productDetailRepo.findAll();
        List<ProductSimple> productSimpleArrayList = new ArrayList<>();
        productByCategory.stream().forEach(e -> {
            ProductSimple productSimple = new ProductSimple();
            BeanUtils.copyProperties(e, productSimple);
            productSimpleArrayList.add(productSimple);
        });
        return ResponseEntity.ok(productSimpleArrayList);
    }

    /**
     * public access
     */
    @GetMapping("productDetails/search")
    public ResponseEntity<?> searchProduct(@RequestParam("key") String key) {
        Optional<List<ProductDetail>> productDetails = productDetailRepo.searchProductByName(key);
        List<ProductSimple> productSimpleArrayList = new ArrayList<>();
        if (productDetails.isEmpty())
            return ResponseEntity.ok(productSimpleArrayList);
        productDetails.get().forEach(e -> {
            ProductSimple productSimple = new ProductSimple();
            BeanUtils.copyProperties(e, productSimple);
            productSimpleArrayList.add(productSimple);
        });
        return ResponseEntity.ok(productSimpleArrayList);
    }

    @PostMapping("productDetails/validate")
    public ResponseEntity<?> validateOrderDetails(@RequestBody List<SnapshotProduct> products) {
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
        Map<String, String> result = new HashMap<>();
        if (containInvalidValue) {
            result.put("result", "false");
        } else {
            result.put("result", "true");
        }
        return ResponseEntity.ok(result);
    }


    /**
     * public access
     */
    @GetMapping("productDetails/{productDetailId}")
    public ResponseEntity<?> getProductById(@PathVariable(name = "productDetailId") Long productDetailId) {
        Optional<ProductDetail> findById = productDetailRepo.findById(productDetailId);
        if (findById.isEmpty())
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(findById.get());
    }


    @PostMapping("productDetails")
    public ResponseEntity<?> createProduct(@RequestHeader("authorization") String authorization, @RequestBody ProductDetail productDetail) {
        ProductDetail save = productDetailRepo.save(productDetail);
        if (productDetail.getOrderStorage() == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().header("Location", save.getId().toString()).build();
    }


    @PutMapping("productDetails/{productDetailId}")
    public ResponseEntity<?> updateProduct(@RequestHeader("authorization") String authorization, @PathVariable(name = "productDetailId") Long productDetailId, @RequestBody ProductDetail newProductDetail) {
        Optional<ProductDetail> findById = productDetailRepo.findById(productDetailId);
        if (findById.isEmpty())
            return ResponseEntity.badRequest().build();
        if (newProductDetail.getOrderStorage() != null)
            return ResponseEntity.badRequest().body("use increaseBy or decreaseBy to update storage value");
        ProductDetail oldProductSimple = findById.get();
        Integer storageCopied = oldProductSimple.getOrderStorage();
        BeanUtils.copyProperties(newProductDetail, oldProductSimple);
        oldProductSimple.setOrderStorage(storageCopied);
        if (newProductDetail.getIncreaseStorageBy() != null)
            oldProductSimple.setOrderStorage(oldProductSimple.getOrderStorage() + newProductDetail.getIncreaseStorageBy());
        if (newProductDetail.getDecreaseStorageBy() != null)
            oldProductSimple.setOrderStorage(oldProductSimple.getOrderStorage() - (newProductDetail.getDecreaseStorageBy()));
        productDetailRepo.save(oldProductSimple);
        return ResponseEntity.ok().build();
    }

    @PutMapping("productDetails/decreaseStorageBy")
    public ResponseEntity<?> decreaseOrderStorage(@RequestHeader("authorization") String authorization, @RequestBody Map<String, String> stringIntegerMapMap) {
        try {
            productService.decreaseOrderStorage(stringIntegerMapMap);
            return ResponseEntity.ok().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping("productDetails/sold")
    public ResponseEntity<?> decreaseActualStorage(@RequestHeader("authorization") String authorization, @RequestBody Map<String, String> stringIntegerMapMap) {
        try {
            productService.decreaseActualStorage(stringIntegerMapMap);
            return ResponseEntity.ok().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping("productDetails/increaseStorageBy")
    public ResponseEntity<?> increaseOrderStorage(@RequestHeader("authorization") String authorization, @RequestBody Map<String, String> stringIntegerMapMap) {
        try {
            productService.increaseOrderStorage(stringIntegerMapMap);
            return ResponseEntity.ok().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }


    @DeleteMapping("productDetails/{productDetailId}")
    public ResponseEntity<?> deleteProduct(@RequestHeader("authorization") String authorization, @PathVariable(name = "productDetailId") Long productDetailId) {
        Optional<ProductDetail> findById = productDetailRepo.findById(productDetailId);
        if (findById.isEmpty())
            return ResponseEntity.badRequest().build();
        productDetailRepo.delete(findById.get());
        return ResponseEntity.ok().build();
    }
}

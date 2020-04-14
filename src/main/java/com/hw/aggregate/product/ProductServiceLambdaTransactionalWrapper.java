package com.hw.aggregate.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
@Service
public class ProductServiceLambdaTransactionalWrapper {

    @Autowired
    private ProductServiceLambda productServiceLambda;

    @Transactional
    public void decreaseActualStorageForMappedProducts(Map<String, String> productMap, String optToken) {
        productServiceLambda.decreaseActualStorageForMappedProducts.accept(productMap, optToken);
    }

    @Transactional
    public void decreaseOrderStorageForMappedProducts(Map<String, String> productMap, String optToken) {
        productServiceLambda.decreaseOrderStorageForMappedProducts.accept(productMap, optToken);
    }

    @Transactional
    public void increaseOrderStorageForMappedProducts(Map<String, String> productMap, String optToken) {
        productServiceLambda.increaseOrderStorageForMappedProducts.accept(productMap, optToken);
    }

    @Transactional
    public void revoke(String optToken) {
        productServiceLambda.revoke.accept(optToken);
    }
}

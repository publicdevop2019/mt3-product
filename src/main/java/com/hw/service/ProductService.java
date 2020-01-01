package com.hw.service;

import com.hw.entity.ProductDetail;
import com.hw.repo.ProductDetailRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    ProductDetailRepo productDetailRepo;

    @Transactional
    public void batchDecrease(Map<Long, Integer> map) throws RuntimeException{
        map.keySet().stream().forEach(productDetailId -> {
            Optional<ProductDetail> findById = productDetailRepo.findById(productDetailId);
            if (findById.isEmpty())
                throw new RuntimeException("product id::" + productDetailId + " not found");
            ProductDetail oldProductSimple = findById.get();
            if (oldProductSimple.getStorage() == null || 0 == oldProductSimple.getStorage())
                throw new RuntimeException("product id::" + productDetailId + " storage is empty");
            Integer output = oldProductSimple.getStorage() - map.get(productDetailId);
            if (output < 0)
                throw new RuntimeException("product id::" + productDetailId + " storage not enough");
            oldProductSimple.setStorage(output);
            productDetailRepo.save(oldProductSimple);
        });
    }
}

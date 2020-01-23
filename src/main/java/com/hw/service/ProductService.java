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

    @Transactional
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

    @Transactional
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
}

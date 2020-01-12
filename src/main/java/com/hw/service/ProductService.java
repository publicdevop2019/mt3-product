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

    /**
     * decrease storage amount and increase sales
     *
     * @param map
     * @throws RuntimeException
     */
    @Transactional
    public void consumeProduct(Map<String, String> map) throws RuntimeException {
        map.keySet().stream().forEach(productDetailId -> {
            Optional<ProductDetail> findById = productDetailRepo.findById(Long.parseLong(productDetailId));
            if (findById.isEmpty())
                throw new RuntimeException("product id::" + productDetailId + " not found");
            ProductDetail oldProductSimple = findById.get();
            if (oldProductSimple.getStorage() == null || 0 == oldProductSimple.getStorage())
                throw new RuntimeException("product id::" + productDetailId + " storage is empty");
            Integer output = oldProductSimple.getStorage() - Integer.parseInt(map.get(productDetailId));
            if (output < 0)
                throw new RuntimeException("product id::" + productDetailId + " storage not enough");
            oldProductSimple.setStorage(output);
            if (oldProductSimple.getSales() == null) {
                oldProductSimple.setSales(Integer.parseInt(map.get(productDetailId)));
            } else {
                oldProductSimple.setSales(oldProductSimple.getSales() + Integer.parseInt(map.get(productDetailId)));
            }
            productDetailRepo.save(oldProductSimple);
        });
    }

    @Transactional
    public void restoreProduct(Map<String, String> map) throws RuntimeException {
        map.keySet().stream().forEach(productDetailId -> {
            Optional<ProductDetail> findById = productDetailRepo.findById(Long.parseLong(productDetailId));
            if (findById.isEmpty())
                throw new RuntimeException("product id::" + productDetailId + " not found");
            ProductDetail oldProductSimple = findById.get();
            if (oldProductSimple.getSales() == null || 0 == oldProductSimple.getSales())
                throw new RuntimeException("product id::" + productDetailId + " sales is empty");
            Integer output = oldProductSimple.getSales() - Integer.parseInt(map.get(productDetailId));
            if (output < 0)
                throw new RuntimeException("product id::" + productDetailId + " sales not enough");
            oldProductSimple.setSales(output);
            oldProductSimple.setStorage(oldProductSimple.getStorage() + Integer.parseInt(map.get(productDetailId)));
            productDetailRepo.save(oldProductSimple);
        });
    }
}

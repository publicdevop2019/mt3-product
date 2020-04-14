package com.hw.aggregate.product;

import com.hw.aggregate.product.command.UpdateProductAdminCommand;
import com.hw.aggregate.product.exception.ProductException;
import com.hw.aggregate.product.model.ProductDetail;
import com.hw.entity.ChangeRecord;
import com.hw.repo.ChangeRepo;
import com.hw.shared.ThrowingBiConsumer;
import com.hw.shared.ThrowingBiFunction;
import com.hw.shared.ThrowingConsumer;
import com.hw.shared.ThrowingFunction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * Transactional will make all fields null
 */
@Service
@Slf4j
public class ProductServiceLambda {
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductDetailRepo productDetailRepo;

    @Autowired
    private ChangeRepo changeRepo;

    private ThrowingBiFunction<Integer, Integer, Integer, ProductException> calcNextStorageValue = (storage, decreaseBy) -> {
        if (0 == storage)
            throw new ProductException("product storage is empty");
        Integer output = storage - decreaseBy;
        if (output < 0)
            throw new ProductException("product storage not enough");
        return output;
    };
    private BiConsumer<ProductDetail, Integer> increaseOrderStorage = (productDetail, increaseBy) -> {
        productDetail.setOrderStorage(productDetail.getOrderStorage() + increaseBy);
        productDetailRepo.save(productDetail);
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

    public ThrowingFunction<Long, ProductDetail, ProductException> getById = (productDetailId) -> {
        Optional<ProductDetail> findById = productDetailRepo.findById(productDetailId);
        if (findById.isEmpty())
            throw new ProductException("productDetailId not found :: " + productDetailId);
        return findById.get();
    };

    public void delete(Long productDetailId) {
        productDetailRepo.delete(getById.apply(productDetailId));
    }


    public ThrowingBiConsumer<Map<String, String>, String, ProductException> increaseOrderStorageForMappedProducts = (map, optToken) -> {
        if (optToken == null) {
            map.keySet().forEach(productDetailId -> {
                getById.andThen(increaseOrderStorage).accept(Long.parseLong(productDetailId), Integer.parseInt(map.get(productDetailId)));
            });
        } else {
            Optional<ChangeRecord> byOptToken = changeRepo.findByOptToken(optToken);
            if (byOptToken.isEmpty()) {
                ChangeRecord change = new ChangeRecord();
                change.setChangeField("actualStorage");
                change.setChangeType("decrease");
                change.setChangeValues(map);
                change.setOptToken(optToken);
                changeRepo.save(change);
                map.keySet().forEach(productDetailId -> {
                    getById.andThen(increaseOrderStorage).accept(Long.parseLong(productDetailId), Integer.parseInt(map.get(productDetailId)));
                });
            }
        }
    };

    public ThrowingBiConsumer<Map<String, String>, String, ProductException> decreaseOrderStorageForMappedProducts = (map, optToken) -> {
        if (optToken == null) {
            map.keySet().forEach(productDetailId -> {
                getById.andThen(decreaseOrderStorage).accept(Long.parseLong(productDetailId), Integer.parseInt(map.get(productDetailId)));
            });
        } else {
            Optional<ChangeRecord> byOptToken = changeRepo.findByOptToken(optToken);
            if (byOptToken.isEmpty()) {
                ChangeRecord change = new ChangeRecord();
                change.setChangeField("orderStorage");
                change.setChangeType("decrease");
                change.setChangeValues(map);
                change.setOptToken(optToken);
                changeRepo.save(change);
                map.keySet().forEach(productDetailId -> {
                    getById.andThen(decreaseOrderStorage).accept(Long.parseLong(productDetailId), Integer.parseInt(map.get(productDetailId)));
                });
            }
        }
    };


    public ThrowingBiConsumer<Map<String, String>, String, ProductException> decreaseActualStorageForMappedProducts = (map, optToken) -> {
        if (optToken == null) {
            map.keySet().forEach(productDetailId -> {
                getById.andThen(decreaseActualStorage).accept(Long.parseLong(productDetailId), Integer.parseInt(map.get(productDetailId)));
            });
        } else {
            Optional<ChangeRecord> byOptToken = changeRepo.findByOptToken(optToken);
            if (byOptToken.isEmpty()) {
                ChangeRecord change = new ChangeRecord();
                change.setChangeField("actualStorage");
                change.setChangeType("decrease");
                change.setChangeValues(map);
                change.setOptToken(optToken);
                changeRepo.save(change);
                map.keySet().forEach(productDetailId -> {
                    synchronized (productDetailRepo) {
                        getById.andThen(decreaseActualStorage).accept(Long.parseLong(productDetailId), Integer.parseInt(map.get(productDetailId)));
                    }
                });
            }
        }
    };

    public ThrowingConsumer<String, ProductException> revoke = (optToken) -> {
        Optional<ChangeRecord> byOptToken = changeRepo.findByOptToken(optToken);
        if (byOptToken.isPresent()) {
            ChangeRecord change = byOptToken.get();
            String changeField = change.getChangeField();
            String changeType = change.getChangeType();
            Map<String, String> changeValue = change.getChangeValues();
            if (changeField.equals("orderStorage")) {
                if (changeType.equals("increase")) {
                    decreaseOrderStorageForMappedProducts.accept(changeValue, null);
                } else if (changeType.equals("decrease")) {
                    increaseOrderStorageForMappedProducts.accept(changeValue, null);
                } else {
                    // do nothing
                }

            } else {
                // do nothing
            }

        }
    };

    public ThrowingBiConsumer<ProductDetail, UpdateProductAdminCommand, ProductException> update = (old, next) -> {
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

}

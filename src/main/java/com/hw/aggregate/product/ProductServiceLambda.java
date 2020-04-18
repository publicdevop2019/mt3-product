package com.hw.aggregate.product;

import com.hw.aggregate.product.command.UpdateProductAdminCommand;
import com.hw.aggregate.product.exception.NotEnoughActualStorageException;
import com.hw.aggregate.product.exception.NotEnoughOrderStorageException;
import com.hw.aggregate.product.exception.ProductNotFoundException;
import com.hw.aggregate.product.model.ProductDetail;
import com.hw.entity.ChangeRecord;
import com.hw.repo.ChangeRepo;
import com.hw.shared.ThrowingBiConsumer;
import com.hw.shared.ThrowingConsumer;
import com.hw.shared.ThrowingFunction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.BiConsumer;

/**
 * Transactional will make all fields null
 */
@Service
@Slf4j
public class ProductServiceLambda {
    @Autowired
    private ProductApplicationService productService;

    @Autowired
    private ProductDetailRepo productDetailRepo;

    @Autowired
    private ChangeRepo changeRepo;

    private BiConsumer<ProductDetail, Integer> increaseOrderStorage = (productDetail, increaseBy) -> {
        productDetail.setOrderStorage(productDetail.getOrderStorage() + increaseBy);
        productDetailRepo.save(productDetail);
    };

    private ThrowingBiConsumer<ProductDetail, Integer, NotEnoughOrderStorageException> decreaseOrderStorage = (pd, decreaseBy) -> {
        Integer apply = pd.getOrderStorage() - decreaseBy;
        log.info("after calc, new order storage value is " + apply);
        if (apply < 0)
            throw new NotEnoughOrderStorageException();
        pd.setOrderStorage(apply);
        productDetailRepo.save(pd);
    };


    private ThrowingBiConsumer<ProductDetail, Integer, RuntimeException> decreaseActualStorage = (pd, decreaseBy) -> {
        Integer apply = pd.getActualStorage() - decreaseBy;
        if (apply < 0)
            throw new NotEnoughActualStorageException();
        pd.setActualStorage(apply);
        if (pd.getSales() == null) {
            pd.setSales(decreaseBy);
        } else {
            pd.setSales(pd.getSales() + decreaseBy);
        }
        productDetailRepo.save(pd);
    };

    public ThrowingFunction<Long, ProductDetail, RuntimeException> getById = (productDetailId) -> {
        Optional<ProductDetail> findById = productDetailRepo.findByIdForUpdate(productDetailId);
        if (findById.isEmpty())
            throw new ProductNotFoundException();
        return findById.get();
    };

    public ThrowingFunction<Long, ProductDetail, RuntimeException> getByIdReadOnly = (productDetailId) -> {
        Optional<ProductDetail> findById = productDetailRepo.findById(productDetailId);
        if (findById.isEmpty())
            throw new ProductNotFoundException();
        return findById.get();
    };

    public ThrowingBiConsumer<Map<String, String>, String, RuntimeException> increaseOrderStorageForMappedProducts = (map, optToken) -> {
        SortedSet<String> keys = new TreeSet<>(map.keySet());
        keys.forEach(productDetailId -> {
            getById.andThen(increaseOrderStorage).accept(Long.parseLong(productDetailId), Integer.parseInt(map.get(productDetailId)));
        });
        if (optToken != null) {
            ChangeRecord change = new ChangeRecord();
            change.setChangeField("orderStorage");
            change.setChangeType("increase");
            change.setChangeValues(map);
            change.setOptToken(optToken);
            changeRepo.save(change);
        }
    };

    public ThrowingBiConsumer<Map<String, String>, String, NotEnoughOrderStorageException> decreaseOrderStorageForMappedProducts = (map, optToken) -> {
        // sort key so deadlock will not happen
        SortedSet<String> keys = new TreeSet<>(map.keySet());
        keys.forEach(productDetailId -> {
            getById.andThen(decreaseOrderStorage).accept(Long.parseLong(productDetailId), Integer.parseInt(map.get(productDetailId)));
        });
        ChangeRecord change = new ChangeRecord();
        change.setChangeField("orderStorage");
        change.setChangeType("decrease");
        change.setChangeValues(map);
        change.setOptToken(optToken);
        changeRepo.save(change);
    };


    public ThrowingBiConsumer<Map<String, String>, String, RuntimeException> decreaseActualStorageForMappedProducts = (map, optToken) -> {
        SortedSet<String> keys = new TreeSet<>(map.keySet());
        keys.forEach(productDetailId -> {
            getById.andThen(decreaseActualStorage).accept(Long.parseLong(productDetailId), Integer.parseInt(map.get(productDetailId)));
        });
        ChangeRecord change = new ChangeRecord();
        change.setChangeField("actualStorage");
        change.setChangeType("decrease");
        change.setChangeValues(map);
        change.setOptToken(optToken);
        changeRepo.save(change);
    };

    public ThrowingConsumer<String, RuntimeException> revoke = (optToken) -> {
        Optional<ChangeRecord> byOptToken = changeRepo.findByOptToken(optToken);
        if (byOptToken.isPresent()) {
            ChangeRecord change = byOptToken.get();
            String changeField = change.getChangeField();
            String changeType = change.getChangeType();
            Map<String, String> changeValue = change.getChangeValues();
            if (changeField.equals("orderStorage")) {
                if (changeType.equals("increase")) {
                    decreaseOrderStorageForMappedProducts.accept(changeValue, optToken + "_revoke_increase");
                } else if (changeType.equals("decrease")) {
                    increaseOrderStorageForMappedProducts.accept(changeValue, optToken + "_revoke_decrease");
                } else {
                    // do nothing
                }

            } else {
                // do nothing
            }

        }
    };

    public ThrowingBiConsumer<ProductDetail, UpdateProductAdminCommand, RuntimeException> update = (old, next) -> {
        Integer orderStorageCopied = old.getOrderStorage();
        Integer actualStorageCopied = old.getActualStorage();
        BeanUtils.copyProperties(next, old);
        old.setOrderStorage(orderStorageCopied);
        old.setActualStorage(actualStorageCopied);
        if (next.getIncreaseOrderStorageBy() != null)
            old.setOrderStorage(old.getOrderStorage() + next.getIncreaseOrderStorageBy());
        if (next.getDecreaseOrderStorageBy() != null) {
            int i = old.getOrderStorage() - next.getDecreaseOrderStorageBy();
            if (i < 0)
                throw new NotEnoughOrderStorageException();
            old.setOrderStorage(i);
        }

        if (next.getIncreaseActualStorageBy() != null)
            old.setActualStorage(old.getActualStorage() + next.getIncreaseActualStorageBy());
        if (next.getDecreaseActualStorageBy() != null) {
            int i = old.getActualStorage() - next.getDecreaseActualStorageBy();
            if (i < 0)
                throw new NotEnoughActualStorageException();
            old.setActualStorage(i);
        }
        productDetailRepo.save(old);
    };

}

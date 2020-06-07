package com.hw.aggregate.product;

import com.hw.aggregate.product.command.UpdateProductAdminCommand;
import com.hw.aggregate.product.exception.*;
import com.hw.aggregate.product.model.ProductDetail;
import com.hw.entity.ChangeRecord;
import com.hw.repo.ChangeRepo;
import com.hw.shared.IdGenerator;
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

    @Autowired
    private IdGenerator idGenerator;

    private ThrowingBiConsumer<ProductDetail, Integer, OrderStorageIncreaseException> increaseOrderStorageNew = (pd, increaseBy) -> {
        Integer integer = productDetailRepo.increaseOrderStorage(pd.getId(), increaseBy);
        if (integer != 1)
            throw new OrderStorageIncreaseException();
    };

    private ThrowingBiConsumer<ProductDetail, Integer, OrderStorageDecreaseException> decreaseOrderStorageNew = (pd, decreaseBy) -> {
        Integer integer = productDetailRepo.decreaseOrderStorage(pd.getId(), decreaseBy);
        if (integer != 1)
            throw new OrderStorageDecreaseException();
    };

    private ThrowingBiConsumer<ProductDetail, Integer, RuntimeException> decreaseActualStorageNew = (pd, decreaseBy) -> {
        Integer integer = productDetailRepo.decreaseActualStorageAndIncreaseSales(pd.getId(), decreaseBy);
        if (integer != 1)
            throw new ActualStorageDecreaseException();
    };
    private ThrowingBiConsumer<ProductDetail, Integer, RuntimeException> increaseActualStorageNew = (pd, decreaseBy) -> {
        Integer integer = productDetailRepo.increaseActualStorageAndDecreaseSales(pd.getId(), decreaseBy);
        if (integer != 1)
            throw new ActualStorageIncreaseException();
    };

    public ThrowingFunction<Long, ProductDetail, RuntimeException> getById = (productDetailId) -> {
        Optional<ProductDetail> findById = productDetailRepo.findById(productDetailId);
        if (findById.isEmpty())
            throw new ProductNotFoundException();
        return findById.get();
    };

    public ThrowingBiConsumer<Map<String, String>, String, RuntimeException> increaseOrderStorageForMappedProducts = (map, optToken) -> {
        SortedSet<String> keys = new TreeSet<>(map.keySet());
        keys.forEach(productDetailId -> {
            getById.andThen(increaseOrderStorageNew).accept(Long.parseLong(productDetailId), Integer.parseInt(map.get(productDetailId)));
        });
        if (optToken != null) {
            ChangeRecord change = new ChangeRecord();
            change.setId(idGenerator.getId());
            change.setChangeField("orderStorage");
            change.setChangeType("increase");
            change.setChangeValues(map);
            change.setOptToken(optToken);
            changeRepo.save(change);
        }
    };

    public ThrowingBiConsumer<Map<String, String>, String, OrderStorageDecreaseException> decreaseOrderStorageForMappedProducts = (map, optToken) -> {
        // sort key so deadlock will not happen
        SortedSet<String> keys = new TreeSet<>(map.keySet());
        keys.forEach(productDetailId ->
                getById.andThen(decreaseOrderStorageNew).accept(Long.parseLong(productDetailId), Integer.parseInt(map.get(productDetailId))));
        ChangeRecord change = new ChangeRecord();
        change.setId(idGenerator.getId());
        change.setChangeField("orderStorage");
        change.setChangeType("decrease");
        change.setChangeValues(map);
        change.setOptToken(optToken);
        changeRepo.save(change);
    };


    public ThrowingBiConsumer<Map<String, String>, String, RuntimeException> decreaseActualStorageForMappedProducts = (map, optToken) -> {
        SortedSet<String> keys = new TreeSet<>(map.keySet());
        keys.forEach(productDetailId -> {
            getById.andThen(decreaseActualStorageNew).accept(Long.parseLong(productDetailId), Integer.parseInt(map.get(productDetailId)));
        });
        ChangeRecord change = new ChangeRecord();
        change.setId(idGenerator.getId());
        change.setChangeField("actualStorage");
        change.setChangeType("decrease");
        change.setChangeValues(map);
        change.setOptToken(optToken);
        changeRepo.save(change);
    };

    public ThrowingBiConsumer<Map<String, String>, String, RuntimeException> increaseActualStorageForMappedProducts = (map, optToken) -> {
        SortedSet<String> keys = new TreeSet<>(map.keySet());
        keys.forEach(productDetailId -> {
            getById.andThen(increaseActualStorageNew).accept(Long.parseLong(productDetailId), Integer.parseInt(map.get(productDetailId)));
        });
        ChangeRecord change = new ChangeRecord();
        change.setId(idGenerator.getId());
        change.setChangeField("actualStorage");
        change.setChangeType("increase");
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
                    log.info("revoke by decrease {}", changeField);
                    decreaseOrderStorageForMappedProducts.accept(changeValue, optToken + "_revoke_increase");
                } else if (changeType.equals("decrease")) {
                    log.info("revoke by increase {}", changeField);
                    increaseOrderStorageForMappedProducts.accept(changeValue, optToken + "_revoke_decrease");
                } else {
                    // do nothing
                }

            }
            if (changeField.equals("actualStorage")) {
                if (changeType.equals("increase")) {
                    decreaseActualStorageForMappedProducts.accept(changeValue, optToken + "_revoke_increase");
                } else if (changeType.equals("decrease")) {
                    increaseActualStorageForMappedProducts.accept(changeValue, optToken + "_revoke_decrease");
                } else {
                    // do nothing
                }

            } else {
                // do nothing
            }

        }
    };
    //@todo review this block, since strategy has changed
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
                throw new OrderStorageDecreaseException();
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

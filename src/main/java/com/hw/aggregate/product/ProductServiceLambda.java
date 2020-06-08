package com.hw.aggregate.product;

import com.hw.aggregate.product.command.UpdateProductAdminCommand;
import com.hw.aggregate.product.exception.*;
import com.hw.aggregate.product.model.ProductDetail;
import com.hw.entity.ChangeRecord;
import com.hw.repo.TransactionHistoryRepository;
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

import static com.hw.aggregate.product.model.AppConstant.*;

/**
 * @note Transactional will make all fields null
 * @note what if rollback request reached first then change request reached ?
 * resource will be changed however this change should not happen.
 * solution: store each transaction in a table, check if rollback exit before execute change
 */
@Service
@Slf4j
public class ProductServiceLambda {
    @Autowired
    private ProductApplicationService productService;

    @Autowired
    private ProductDetailRepo productDetailRepo;

    @Autowired
    private TransactionHistoryRepository txRepo;

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

    public ThrowingBiConsumer<Map<String, String>, String, RuntimeException> increaseOrderStorageForMappedProducts = (map, txId) -> {
        if (txRepo.findByOptToken(txId + REVOKE).isPresent()) {
            throw new HangingTransactionException();
        }
        SortedSet<String> keys = new TreeSet<>(map.keySet());
        keys.forEach(productDetailId -> {
            getById.andThen(increaseOrderStorageNew).accept(Long.parseLong(productDetailId), Integer.parseInt(map.get(productDetailId)));
        });
        if (txId != null) {
            ChangeRecord change = new ChangeRecord();
            change.setId(idGenerator.getId());
            change.setChangeField(ORDER_STORAGE);
            change.setChangeType(INCREASE);
            change.setChangeValues(map);
            change.setOptToken(txId);
            txRepo.save(change);
        }
    };

    public ThrowingBiConsumer<Map<String, String>, String, OrderStorageDecreaseException> decreaseOrderStorageForMappedProducts = (map, txId) -> {
        if (txRepo.findByOptToken(txId + REVOKE).isPresent()) {
            throw new HangingTransactionException();
        }
        // sort key so deadlock will not happen
        SortedSet<String> keys = new TreeSet<>(map.keySet());
        keys.forEach(productDetailId ->
                getById.andThen(decreaseOrderStorageNew).accept(Long.parseLong(productDetailId), Integer.parseInt(map.get(productDetailId))));
        ChangeRecord change = new ChangeRecord();
        change.setId(idGenerator.getId());
        change.setChangeField(ORDER_STORAGE);
        change.setChangeType(DECREASE);
        change.setChangeValues(map);
        change.setOptToken(txId);
        txRepo.save(change);
    };


    public ThrowingBiConsumer<Map<String, String>, String, RuntimeException> decreaseActualStorageForMappedProducts = (map, txId) -> {
        if (txRepo.findByOptToken(txId + REVOKE).isPresent()) {
            throw new HangingTransactionException();
        }
        SortedSet<String> keys = new TreeSet<>(map.keySet());
        keys.forEach(productDetailId -> {
            getById.andThen(decreaseActualStorageNew).accept(Long.parseLong(productDetailId), Integer.parseInt(map.get(productDetailId)));
        });
        ChangeRecord change = new ChangeRecord();
        change.setId(idGenerator.getId());
        change.setChangeField(ACTUAL_STORAGE);
        change.setChangeType(DECREASE);
        change.setChangeValues(map);
        change.setOptToken(txId);
        txRepo.save(change);
    };

    public ThrowingBiConsumer<Map<String, String>, String, RuntimeException> increaseActualStorageForMappedProducts = (map, txId) -> {
        if (txRepo.findByOptToken(txId + REVOKE).isPresent()) {
            throw new HangingTransactionException();
        }
        SortedSet<String> keys = new TreeSet<>(map.keySet());
        keys.forEach(productDetailId -> {
            getById.andThen(increaseActualStorageNew).accept(Long.parseLong(productDetailId), Integer.parseInt(map.get(productDetailId)));
        });
        ChangeRecord change = new ChangeRecord();
        change.setId(idGenerator.getId());
        change.setChangeField(ACTUAL_STORAGE);
        change.setChangeType(INCREASE);
        change.setChangeValues(map);
        change.setOptToken(txId);
        txRepo.save(change);
    };

    public ThrowingConsumer<String, RuntimeException> revoke = (txId) -> {
        Optional<ChangeRecord> byOptToken = txRepo.findByOptToken(txId);
        if (byOptToken.isPresent()) {
            ChangeRecord change = byOptToken.get();
            String changeField = change.getChangeField();
            String changeType = change.getChangeType();
            Map<String, String> changeValue = change.getChangeValues();
            if (changeField.equals(ORDER_STORAGE)) {
                if (changeType.equals(INCREASE)) {
                    log.info("revoke by decrease {}", changeField);
                    decreaseOrderStorageForMappedProducts.accept(changeValue, txId + REVOKE);
                } else if (changeType.equals(DECREASE)) {
                    log.info("revoke by increase {}", changeField);
                    increaseOrderStorageForMappedProducts.accept(changeValue, txId + REVOKE);
                } else {
                    // do nothing
                }

            }
            if (changeField.equals(ACTUAL_STORAGE)) {
                if (changeType.equals(INCREASE)) {
                    decreaseActualStorageForMappedProducts.accept(changeValue, txId + REVOKE);
                } else if (changeType.equals(DECREASE)) {
                    increaseActualStorageForMappedProducts.accept(changeValue, txId + REVOKE);
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

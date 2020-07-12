package com.hw.aggregate.product;

import com.hw.aggregate.product.command.DecreaseActualStorageCommand;
import com.hw.aggregate.product.command.DecreaseOrderStorageCommand;
import com.hw.aggregate.product.command.IncreaseActualStorageCommand;
import com.hw.aggregate.product.command.IncreaseOrderStorageCommand;
import com.hw.aggregate.product.exception.*;
import com.hw.aggregate.product.model.*;
import com.hw.shared.IdGenerator;
import com.hw.shared.ThrowingConsumer;
import com.hw.shared.ThrowingFunction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.function.BiFunction;

import static com.hw.config.AppConstant.*;

/**
 * @note Transactional will make all fields null
 * @note what if rollback request reached first then change request reached ?
 * resource will be changed however this change should not happen.
 * solution: store each transaction in a table, check if rollback exit before making any change
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

    @Autowired
    private EntityManager entityManager;

    private BiFunction<StorageChangeDetail, String, Integer> executeStorageChange = (changeDetail, nativeQuery) -> {
        //sort to make sure order is fixed
        TreeSet<String> sorted = new TreeSet<>(changeDetail.getAttributeSales());
        String collect = String.join(",", sorted);
        int i = entityManager.createNativeQuery(nativeQuery)
                .setParameter(1, changeDetail.getAmount())
                .setParameter(2, changeDetail.getProductId())
                .setParameter(3, collect)
                .executeUpdate();
        return i;
    };

    private ThrowingConsumer<StorageChangeDetail, OrderStorageIncreaseException> increaseOrderStorage = (changeDetail) -> {
        Integer apply = executeStorageChange.apply(changeDetail, "UPDATE product_sku_map AS p " +
                "SET p.storage_order = p.storage_order + ?1 " +
                "WHERE p.product_id = ?2 AND p.attributes_sales = ?3");
        if (!apply.equals(1))
            throw new OrderStorageIncreaseException();
    };

    private ThrowingConsumer<StorageChangeDetail, OrderStorageDecreaseException> decreaseOrderStorage = (changeDetail) -> {
        Integer apply = executeStorageChange.apply(changeDetail, "UPDATE product_sku_map AS p " +
                "SET p.storage_order = p.storage_order - ?1 " +
                "WHERE p.product_id = ?2 AND p.attributes_sales = ?3 AND p.storage_order - ?1 >= 0");
        if (!apply.equals(1))
            throw new OrderStorageDecreaseException();
    };

    private ThrowingConsumer<StorageChangeDetail, RuntimeException> decreaseActualStorage = (changeDetail) -> {
        Integer apply = executeStorageChange.apply(changeDetail, "UPDATE product_sku_map AS p " +
                "SET p.storage_actual = p.storage_actual - ?1 , p.sales = p.sales + ?1 " +
                "WHERE p.product_id = ?2 AND p.attributes_sales = ?3 AND p.storage_actual - ?1 >= 0");
        if (!apply.equals(1))
            throw new ActualStorageDecreaseException();
    };
    private ThrowingConsumer<StorageChangeDetail, RuntimeException> increaseActualStorage = (changeDetail) -> {
        Integer apply = executeStorageChange.apply(changeDetail, "UPDATE product_sku_map AS p " +
                "SET p.storage_actual = p.storage_actual + ?1 , p.sales = p.sales - ?1 " +
                "WHERE p.product_id = ?2 AND p.attributes_sales = ?3 AND p.sales - ?1 >= 0");
        if (!apply.equals(1))
            throw new ActualStorageIncreaseException();
    };

    private ThrowingConsumer<StorageChangeDetail, RuntimeException> adminDecreaseActualStorage = (changeDetail) -> {
        Integer apply = executeStorageChange.apply(changeDetail, "UPDATE product_sku_map AS p " +
                "SET p.storage_actual = p.storage_actual - ?1 " +
                "WHERE p.product_id = ?2 AND p.attributes_sales = ?3 AND p.storage_actual - ?1 >= 0");
        if (!apply.equals(1))
            throw new ActualStorageDecreaseException();
    };
    private ThrowingConsumer<StorageChangeDetail, RuntimeException> adminIncreaseActualStorage = (changeDetail) -> {
        Integer apply = executeStorageChange.apply(changeDetail, "UPDATE product_sku_map AS p " +
                "SET p.storage_actual = p.storage_actual + ?1 " +
                "WHERE p.product_id = ?2 AND p.attributes_sales = ?3");
        if (!apply.equals(1))
            throw new ActualStorageIncreaseException();
    };


    public ThrowingFunction<Long, ProductDetail, RuntimeException> getByIdForAdmin = (productDetailId) -> {
        Optional<ProductDetail> findById = productDetailRepo.findById(productDetailId);
        if (findById.isEmpty())
            throw new ProductNotFoundException();
        return findById.get();
    };
    public ThrowingFunction<Long, ProductDetail, RuntimeException> getByIdForCustomer = (productDetailId) -> {
        Optional<ProductDetail> findById = productDetailRepo.findById(productDetailId);
        if (findById.isEmpty())
            throw new ProductNotFoundException();
        if (findById.get().getStatus().equals(ProductStatus.UNAVAILABLE))
            throw new ProductNotAvailableException();
        return findById.get();
    };

    public ThrowingConsumer<IncreaseOrderStorageCommand, RuntimeException> increaseOrderStorageForMappedProducts = (command) -> {
        beforeUpdateStorage(command);
        command.getChangeList().forEach(changeDetail -> increaseOrderStorage.accept(changeDetail));
        SaveTx(ORDER_STORAGE, INCREASE, command.getChangeList(), command.getTxId());
    };

    public ThrowingConsumer<DecreaseOrderStorageCommand, OrderStorageDecreaseException> decreaseOrderStorageForMappedProducts = (command) -> {
        beforeUpdateStorage(command);
        command.getChangeList().forEach(changeDetail -> decreaseOrderStorage.accept(changeDetail));
        SaveTx(ORDER_STORAGE, DECREASE, command.getChangeList(), command.getTxId());
    };


    public ThrowingConsumer<DecreaseActualStorageCommand, RuntimeException> decreaseActualStorageForMappedProducts = (command) -> {
        beforeUpdateStorage(command);
        command.getChangeList().forEach(changeDetail -> decreaseActualStorage.accept(changeDetail));
        SaveTx(ACTUAL_STORAGE, DECREASE, command.getChangeList(), command.getTxId());
    };
    public ThrowingConsumer<DecreaseActualStorageCommand, RuntimeException> adminDecreaseActualStorageForMappedProducts = (command) -> {
        beforeUpdateStorage(command);
        command.getChangeList().forEach(changeDetail -> adminDecreaseActualStorage.accept(changeDetail));
        SaveTx(ACTUAL_STORAGE, DECREASE, command.getChangeList(), command.getTxId());
    };

    public ThrowingConsumer<IncreaseActualStorageCommand, RuntimeException> increaseActualStorageForMappedProducts = (command) -> {
        beforeUpdateStorage(command);
        command.getChangeList().forEach(changeDetail -> increaseActualStorage.accept(changeDetail));
        SaveTx(ACTUAL_STORAGE, INCREASE, command.getChangeList(), command.getTxId());
    };
    public ThrowingConsumer<IncreaseActualStorageCommand, RuntimeException> adminIncreaseActualStorageForMappedProducts = (command) -> {
        beforeUpdateStorage(command);
        command.getChangeList().forEach(changeDetail -> adminIncreaseActualStorage.accept(changeDetail));
        SaveTx(ACTUAL_STORAGE, INCREASE, command.getChangeList(), command.getTxId());
    };

    private void beforeUpdateStorage(StorageChangeCommon common) {
        if (txRepo.findByTransactionId(common.getTxId() + REVOKE).isPresent()) {
            throw new HangingTransactionException();
        }
        // sort key so deadlock will not happen
        Collections.sort(common.getChangeList());
    }

    private void SaveTx(String changeField, String changeType, List<StorageChangeDetail> details, String txId) {
        TransactionRecord tx = new TransactionRecord();
        tx.setId(idGenerator.getId());
        tx.setChangeField(changeField);
        tx.setChangeType(changeType);
        tx.setChangeValues(new ArrayList<>(details));
        tx.setTransactionId(txId);
        txRepo.save(tx);
    }

    public ThrowingConsumer<String, RuntimeException> rollbackTx = (txId) -> {
        if (txRepo.findByTransactionId(txId + REVOKE).isPresent()) {
            throw new HangingTransactionException();
        }
        Optional<TransactionRecord> byOptToken = txRepo.findByTransactionId(txId);
        if (byOptToken.isPresent()) {
            TransactionRecord tr = byOptToken.get();
            String changeField = tr.getChangeField();
            String changeType = tr.getChangeType();
            List<StorageChangeDetail> changeValue = tr.getChangeValues();
            if (changeField.equals(ORDER_STORAGE)) {
                if (changeType.equals(INCREASE)) {
                    log.info("revoke orderStorage by decrease {}", changeField);
                    DecreaseOrderStorageCommand command = new DecreaseOrderStorageCommand();
                    command.setTxId(txId + REVOKE);
                    command.setChangeList(changeValue);
                    decreaseOrderStorageForMappedProducts.accept(command);
                } else if (changeType.equals(DECREASE)) {
                    log.info("revoke orderStorage by increase {}", changeField);
                    IncreaseOrderStorageCommand command = new IncreaseOrderStorageCommand();
                    command.setTxId(txId + REVOKE);
                    command.setChangeList(changeValue);
                    increaseOrderStorageForMappedProducts.accept(command);
                } else {
                    // do nothing
                }

            }
            if (changeField.equals(ACTUAL_STORAGE)) {
                if (changeType.equals(INCREASE)) {
                    log.info("revoke actualStorage by decrease {}", changeField);
                    DecreaseActualStorageCommand command = new DecreaseActualStorageCommand();
                    command.setTxId(txId + REVOKE);
                    command.setChangeList(changeValue);
                    decreaseActualStorageForMappedProducts.accept(command);
                } else if (changeType.equals(DECREASE)) {
                    log.info("revoke actualStorage by increase {}", changeField);
                    IncreaseActualStorageCommand command = new IncreaseActualStorageCommand();
                    command.setTxId(txId + REVOKE);
                    command.setChangeList(changeValue);
                    increaseActualStorageForMappedProducts.accept(command);
                } else {
                    // do nothing
                }

            } else {
                // do nothing
            }

        }
    };
}

package com.hw.aggregate.product;

import com.hw.aggregate.product.command.DecreaseActualStorageCommand;
import com.hw.aggregate.product.command.DecreaseOrderStorageCommand;
import com.hw.aggregate.product.command.IncreaseActualStorageCommand;
import com.hw.aggregate.product.command.IncreaseOrderStorageCommand;
import com.hw.aggregate.product.exception.*;
import com.hw.aggregate.product.model.ProductDetail;
import com.hw.aggregate.product.model.StorageChangeDetail;
import com.hw.aggregate.product.model.TransactionRecord;
import com.hw.shared.IdGenerator;
import com.hw.shared.ThrowingBiConsumer;
import com.hw.shared.ThrowingConsumer;
import com.hw.shared.ThrowingFunction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.*;

import static com.hw.config.AppConstant.*;

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

    @Autowired
    private EntityManager entityManager;

    private ThrowingConsumer<StorageChangeDetail, OrderStorageIncreaseException> increaseOrderStorage = (changeDetail) -> {
        //sort to make sure order is fixed
        TreeSet sorted = new TreeSet(changeDetail.getAttributeSales());
        int i = entityManager.createNativeQuery(
                "UPDATE product_sku_map AS p " +
                        "SET p.storage_order = p.storage_order + ?1 " +
                        "WHERE p.product_id = ?2 AND p.attributes_sales = ?3 ")
                .setParameter(1, changeDetail.getAmount())
                .setParameter(2, changeDetail.getProductId())
                .setParameter(3, sorted)
                .executeUpdate();
        if (i != 1)
            throw new OrderStorageIncreaseException();
    };

    private ThrowingConsumer<StorageChangeDetail, OrderStorageDecreaseException> decreaseOrderStorage = (changeDetail) -> {
        //sort to make sure order is fixed
        TreeSet sorted = new TreeSet(changeDetail.getAttributeSales());
        int i = entityManager.createNativeQuery(
                "UPDATE product_sku_map AS p " +
                        "SET p.storage_order = p.storage_order - ?1 " +
                        "WHERE p.product_id = ?2 AND p.attributes_sales = ?3 AND p.storage_order - ?1 >= 0")
                .setParameter(1, changeDetail.getAmount())
                .setParameter(2, changeDetail.getProductId())
                .setParameter(3, sorted)
                .executeUpdate();
        if (i != 1)
            throw new OrderStorageDecreaseException();
    };

    private ThrowingConsumer<StorageChangeDetail, RuntimeException> decreaseActualStorage = (changeDetail) -> {
        //sort to make sure order is fixed
        TreeSet sorted = new TreeSet(changeDetail.getAttributeSales());
        int i = entityManager.createNativeQuery(
                "UPDATE product_sku_map AS p " +
                        "SET p.storage_actual = p.storage_actual - ?1 , p.sales = p.sales + ?2 " +
                        "WHERE p.product_id = ?2 AND p.attributes_sales = ?3 AND p.storage_actual - ?1 >= 0")
                .setParameter(1, changeDetail.getAmount())
                .setParameter(2, changeDetail.getProductId())
                .setParameter(3, sorted)
                .executeUpdate();
        if (i != 1)
            throw new ActualStorageDecreaseException();
    };
    private ThrowingConsumer<StorageChangeDetail, RuntimeException> increaseActualStorage = (changeDetail) -> {
        //sort to make sure order is fixed
        TreeSet sorted = new TreeSet(changeDetail.getAttributeSales());
        int i = entityManager.createNativeQuery(
                "UPDATE product_sku_map AS p " +
                        "SET p.storage_actual = p.storage_actual + ?1 , p.sales = p.sales - ?2 " +
                        "WHERE p.product_id = ?2 AND p.attributes_sales = ?3 AND p.sales - ?1 >= 0")
                .setParameter(1, changeDetail.getAmount())
                .setParameter(2, changeDetail.getProductId())
                .setParameter(3, sorted)
                .executeUpdate();
        if (i != 1)
            throw new ActualStorageIncreaseException();
    };

    public ThrowingFunction<Long, ProductDetail, RuntimeException> getById = (productDetailId) -> {
        Optional<ProductDetail> findById = productDetailRepo.findById(productDetailId);
        if (findById.isEmpty())
            throw new ProductNotFoundException();
        return findById.get();
    };

    public ThrowingConsumer<IncreaseOrderStorageCommand, RuntimeException> increaseOrderStorageForMappedProducts = (command) -> {
        if (txRepo.findByTransactionId(command.getTxId() + REVOKE).isPresent()) {
            throw new HangingTransactionException();
        }
        // sort key so deadlock will not happen
        Collections.sort(command.getChangeList());
        command.getChangeList().forEach(changeDetail -> increaseOrderStorage.accept(changeDetail));
        TransactionRecord tx = new TransactionRecord();
        tx.setId(idGenerator.getId());
        tx.setChangeField(ORDER_STORAGE);
        tx.setChangeType(INCREASE);
        tx.setChangeValues(new ArrayList<>(command.getChangeList()));
        tx.setTransactionId(command.getTxId());
        txRepo.save(tx);
    };

    public ThrowingConsumer<DecreaseOrderStorageCommand, OrderStorageDecreaseException> decreaseOrderStorageForMappedProducts = (command) -> {
        if (txRepo.findByTransactionId(command.getTxId() + REVOKE).isPresent()) {
            throw new HangingTransactionException();
        }
        // sort key so deadlock will not happen
        Collections.sort(command.getChangeList());
        command.getChangeList().forEach(changeDetail -> decreaseOrderStorage.accept(changeDetail));
        TransactionRecord tx = new TransactionRecord();
        tx.setId(idGenerator.getId());
        tx.setChangeField(ORDER_STORAGE);
        tx.setChangeType(DECREASE);
        tx.setChangeValues(new ArrayList<>(command.getChangeList()));
        tx.setTransactionId(command.getTxId());
        txRepo.save(tx);
    };


    public ThrowingConsumer<DecreaseActualStorageCommand, RuntimeException> decreaseActualStorageForMappedProducts = (command) -> {
        if (txRepo.findByTransactionId(command.getTxId() + REVOKE).isPresent()) {
            throw new HangingTransactionException();
        }
        // sort key so deadlock will not happen
        Collections.sort(command.getChangeList());
        command.getChangeList().forEach(changeDetail -> decreaseActualStorage.accept(changeDetail));
        TransactionRecord tx = new TransactionRecord();
        tx.setId(idGenerator.getId());
        tx.setChangeField(ACTUAL_STORAGE);
        tx.setChangeType(DECREASE);
        tx.setChangeValues(new ArrayList<>(command.getChangeList()));
        tx.setTransactionId(command.getTxId());
        txRepo.save(tx);
    };

    public ThrowingConsumer<IncreaseActualStorageCommand, RuntimeException> increaseActualStorageForMappedProducts = (command) -> {
        if (txRepo.findByTransactionId(command.getTxId() + REVOKE).isPresent()) {
            throw new HangingTransactionException();
        }
        // sort key so deadlock will not happen
        Collections.sort(command.getChangeList());
        command.getChangeList().forEach(changeDetail -> increaseActualStorage.accept(changeDetail));
        TransactionRecord tx = new TransactionRecord();
        tx.setId(idGenerator.getId());
        tx.setChangeField(ACTUAL_STORAGE);
        tx.setChangeType(INCREASE);
        tx.setChangeValues(new ArrayList<>(command.getChangeList()));
        tx.setTransactionId(command.getTxId());
        txRepo.save(tx);
    };

    public ThrowingConsumer<String, RuntimeException> rollbackTx = (txId) -> {
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

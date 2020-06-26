package com.hw.aggregate.product;

import com.hw.aggregate.product.command.IncreaseOrderStorageCommand;
import com.hw.aggregate.product.exception.HangingTransactionException;
import com.hw.aggregate.product.exception.OrderStorageDecreaseException;
import com.hw.aggregate.product.exception.OrderStorageIncreaseException;
import com.hw.aggregate.product.exception.ProductNotFoundException;
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

    private ThrowingConsumer<StorageChangeDetail, OrderStorageIncreaseException> increaseOrderStorageNew = (changeDetail) -> {
        //sort to prevent dead lock & make sure order is fixed
        TreeSet sorted = new TreeSet(changeDetail.getAttributeSales());
        int i = entityManager.createNativeQuery(
                "UPDATE product_sku_map AS p " +
                        "SET p.storage_order = p.storage_order + ?1" +
                        "WHERE p.product_id = ?2 AND p.attribute_sales = ?3 ")
                .setParameter(1, changeDetail.getAmount())
                .setParameter(2, changeDetail.getProductId())
                .setParameter(3, sorted)
                .executeUpdate();
        if (i != 1)
            throw new OrderStorageIncreaseException();
    };

    private ThrowingBiConsumer<ProductDetail, Integer, OrderStorageDecreaseException> decreaseOrderStorageNew = (pd, decreaseBy) -> {
//        Integer integer = productDetailRepo.decreaseOrderStorage(pd.getId(), decreaseBy);
//        if (integer != 1)
//            throw new OrderStorageDecreaseException();
    };

    private ThrowingBiConsumer<ProductDetail, Integer, RuntimeException> decreaseActualStorageNew = (pd, decreaseBy) -> {
//        Integer integer = productDetailRepo.decreaseActualStorageAndIncreaseSales(pd.getId(), decreaseBy);
//        if (integer != 1)
//            throw new ActualStorageDecreaseException();
    };
    private ThrowingBiConsumer<ProductDetail, Integer, RuntimeException> increaseActualStorageNew = (pd, decreaseBy) -> {
//        Integer integer = productDetailRepo.increaseActualStorageAndDecreaseSales(pd.getId(), decreaseBy);
//        if (integer != 1)
//            throw new ActualStorageIncreaseException();
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
        Collections.sort(command.getChangeList());
        command.getChangeList().forEach(changeDetail -> increaseOrderStorageNew.accept(changeDetail));
        if (command.getTxId() != null) {
            TransactionRecord change = new TransactionRecord();
            change.setId(idGenerator.getId());
            change.setChangeField(ORDER_STORAGE);
            change.setChangeType(INCREASE);
            HashMap<Long, Integer> longIntegerHashMap = new HashMap<>();
            command.getChangeList().forEach(e -> {
                if (longIntegerHashMap.containsKey(e.getProductId())) {
                    Integer stored = longIntegerHashMap.get(e.getProductId());
                    longIntegerHashMap.put(e.getProductId(), Integer.sum(stored, e.getAmount()));
                } else {
                    longIntegerHashMap.put(e.getProductId(), e.getAmount());
                }
            });
            change.setChangeValues(longIntegerHashMap);
            change.setTransactionId(command.getTxId());
            txRepo.save(change);
        }
    };

    public ThrowingBiConsumer<Map<String, String>, String, OrderStorageDecreaseException> decreaseOrderStorageForMappedProducts = (map, txId) -> {
//        if (txRepo.findByTransactionId(txId + REVOKE).isPresent()) {
//            throw new HangingTransactionException();
//        }
//        // sort key so deadlock will not happen
//        Map<String, String> treeMap = new TreeMap<>(map);
//        treeMap.keySet().forEach(productDetailId ->
//                getById.andThen(decreaseOrderStorageNew).accept(Long.parseLong(productDetailId), Integer.parseInt(map.get(productDetailId))));
//        TransactionRecord change = new TransactionRecord();
//        change.setId(idGenerator.getId());
//        change.setChangeField(ORDER_STORAGE);
//        change.setChangeType(DECREASE);
//        change.setChangeValues(treeMap);
//        change.setTransactionId(txId);
//        txRepo.save(change);
    };


    public ThrowingBiConsumer<Map<String, String>, String, RuntimeException> decreaseActualStorageForMappedProducts = (map, txId) -> {
//        if (txRepo.findByTransactionId(txId + REVOKE).isPresent()) {
//            throw new HangingTransactionException();
//        }
//        Map<String, String> treeMap = new TreeMap<>(map);
//        treeMap.keySet().forEach(productDetailId -> {
//            getById.andThen(decreaseActualStorageNew).accept(Long.parseLong(productDetailId), Integer.parseInt(map.get(productDetailId)));
//        });
//        TransactionRecord tx = new TransactionRecord();
//        tx.setId(idGenerator.getId());
//        tx.setChangeField(ACTUAL_STORAGE);
//        tx.setChangeType(DECREASE);
//        tx.setChangeValues(treeMap);
//        tx.setTransactionId(txId);
//        txRepo.save(tx);
    };

    public ThrowingBiConsumer<Map<String, String>, String, RuntimeException> increaseActualStorageForMappedProducts = (map, txId) -> {
//        if (txRepo.findByTransactionId(txId + REVOKE).isPresent()) {
//            throw new HangingTransactionException();
//        }
//        Map<String, String> treeMap = new TreeMap<>(map);
//        treeMap.keySet().forEach(productDetailId -> {
//            getById.andThen(increaseActualStorageNew).accept(Long.parseLong(productDetailId), Integer.parseInt(map.get(productDetailId)));
//        });
//        TransactionRecord change = new TransactionRecord();
//        change.setId(idGenerator.getId());
//        change.setChangeField(ACTUAL_STORAGE);
//        change.setChangeType(INCREASE);
//        change.setChangeValues(treeMap);
//        change.setTransactionId(txId);
//        txRepo.save(change);
    };

    public ThrowingConsumer<String, RuntimeException> revoke = (txId) -> {
//        Optional<TransactionRecord> byOptToken = txRepo.findByTransactionId(txId);
//        if (byOptToken.isPresent()) {
//            TransactionRecord change = byOptToken.get();
//            String changeField = change.getChangeField();
//            String changeType = change.getChangeType();
//            Map<Long, Integer> changeValue = change.getChangeValues();
//            if (changeField.equals(ORDER_STORAGE)) {
//                if (changeType.equals(INCREASE)) {
//                    log.info("revoke orderStorage by decrease {}", changeField);
//                    decreaseOrderStorageForMappedProducts.accept(changeValue, txId + REVOKE);
//                } else if (changeType.equals(DECREASE)) {
//                    log.info("revoke orderStorage by increase {}", changeField);
//                    increaseOrderStorageForMappedProducts.accept(changeValue, txId + REVOKE);
//                } else {
//                    // do nothing
//                }
//
//            }
//            if (changeField.equals(ACTUAL_STORAGE)) {
//                if (changeType.equals(INCREASE)) {
//                    log.info("revoke actualStorage by decrease {}", changeField);
//                    decreaseActualStorageForMappedProducts.accept(changeValue, txId + REVOKE);
//                } else if (changeType.equals(DECREASE)) {
//                    log.info("revoke actualStorage by increase {}", changeField);
//                    increaseActualStorageForMappedProducts.accept(changeValue, txId + REVOKE);
//                } else {
//                    // do nothing
//                }
//
//            } else {
//                // do nothing
//            }
//
//        }
    };
}

package com.hw.service;

import com.hw.Product;
import com.hw.clazz.OptionItem;
import com.hw.clazz.ProductOption;
import com.hw.entity.ProductDetail;
import com.hw.entity.SnapshotProduct;
import com.hw.repo.ProductDetailRepo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Product.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductServiceTest {

    @Autowired
    ProductService productService;

    @Autowired
    ProductServiceTransactional productServiceTransactional;

    @LocalServerPort
    int randomServerPort;

    TestRestTemplate testRestTemplate = new TestRestTemplate();

    @Autowired
    ProductDetailRepo productDetailRepo;

    @Test
    public void concurrentValidation() {
        String url = "http://localhost:" + randomServerPort + "/v1/api/productDetails/validate";
        OptionItem optionItem = new OptionItem("value1", null);

        ProductOption reqAddOn1 = new ProductOption();
        reqAddOn1.setTitle("option1");
        ArrayList<OptionItem> optionItems = new ArrayList<>();

        reqAddOn1.setOptions(optionItems);
        reqAddOn1.getOptions().add(optionItem);
        ProductOption reqAddOn2 = new ProductOption();
        reqAddOn2.setTitle("option2");
        reqAddOn2.setOptions(optionItems);
        reqAddOn2.getOptions().add(optionItem);

        SnapshotProduct snapshotProduct = new SnapshotProduct();
        snapshotProduct.setFinalPrice("102");
        snapshotProduct.setProductId("0");
        ArrayList<ProductOption> productOptions = new ArrayList<>();
        snapshotProduct.setSelectedOptions(productOptions);
        snapshotProduct.getSelectedOptions().add(reqAddOn1);
        snapshotProduct.getSelectedOptions().add(reqAddOn2);

        ArrayList<SnapshotProduct> products = new ArrayList<>();
        products.add(snapshotProduct);
        products.add(snapshotProduct);
        products.add(snapshotProduct);
        products.add(snapshotProduct);
        HttpEntity<List<SnapshotProduct>> listHttpEntity = new HttpEntity<>(products, null);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                testRestTemplate.exchange(url, HttpMethod.POST, listHttpEntity, Object.class);
            }
        };
        ArrayList<Runnable> runnables = new ArrayList<>();
        IntStream.range(0, 5).forEach(e -> {
            runnables.add(runnable);
        });
        try {
            assertConcurrent("", runnables, 30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void concurrentDecrease() {
        Integer threadCount = 20;
        String productId = "0";
        Integer iniOrderStorage = productDetailRepo.findById(Long.parseLong(productId)).get().getOrderStorage();
        Integer expected = iniOrderStorage - threadCount;
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put(productId, "1");
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                productService.decreaseOrderStorageForMappedProducts.accept(stringStringHashMap);
            }
        };
        ArrayList<Runnable> runnables = new ArrayList<>();
        IntStream.range(0, threadCount).forEach(e -> {
            runnables.add(runnable);
        });
        try {
            assertConcurrent("", runnables, 30000);
            assertTrue("remain storage should be " + expected, productDetailRepo.findById(Long.parseLong(productId)).get().getOrderStorage().equals(expected));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void transactionalDecreaseEnough() {
        String productId1 = "0";
        String productId2 = "1";
        Integer decreaseProd1By = 1;
        Integer decreaseProd2By = 1;
        Integer iniOrderStorage1 = productDetailRepo.findById(Long.parseLong(productId1)).get().getOrderStorage();
        Integer iniOrderStorage2 = productDetailRepo.findById(Long.parseLong(productId2)).get().getOrderStorage();
        Integer expected1 = iniOrderStorage1 - decreaseProd1By;
        Integer expected2 = iniOrderStorage2 - decreaseProd1By;
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put(productId1, String.valueOf(decreaseProd1By));
        stringStringHashMap.put(productId2, String.valueOf(decreaseProd2By));
        try {

            productService.decreaseOrderStorageForMappedProducts.accept(stringStringHashMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue("storage should not match", productDetailRepo.findById(Long.parseLong(productId1)).get().getOrderStorage().equals(expected1));
        assertTrue("storage should not match", productDetailRepo.findById(Long.parseLong(productId2)).get().getOrderStorage().equals(expected2));
    }

    @Test
    public void transactionalDecreaseNotEnough() {
        String productId1 = "0";
        String productId2 = "1";
        Integer iniOrderStorage1 = productDetailRepo.findById(Long.parseLong(productId1)).get().getOrderStorage();
        Integer iniOrderStorage2 = productDetailRepo.findById(Long.parseLong(productId2)).get().getOrderStorage();
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put(productId1, String.valueOf(iniOrderStorage1 - 1));
        stringStringHashMap.put(productId2, String.valueOf(iniOrderStorage2 + 1));
        try {
            productServiceTransactional.decreaseOrderStorageForMappedProducts(stringStringHashMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue("storage should not change", productDetailRepo.findById(Long.parseLong(productId1)).get().getOrderStorage().equals(iniOrderStorage1));
        assertTrue("storage should not change", productDetailRepo.findById(Long.parseLong(productId2)).get().getOrderStorage().equals(iniOrderStorage2));
    }

    /**
     * copied from https://www.planetgeek.ch/2009/08/25/how-to-find-a-concurrency-bug-with-java/
     *
     * @param message
     * @param runnables
     * @param maxTimeoutSeconds
     * @throws InterruptedException
     */
    public static void assertConcurrent(final String message, final List<? extends Runnable> runnables, final int maxTimeoutSeconds) throws InterruptedException {
        final int numThreads = runnables.size();
        final List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<Throwable>());
        final ExecutorService threadPool = Executors.newFixedThreadPool(numThreads);
        try {
            final CountDownLatch allExecutorThreadsReady = new CountDownLatch(numThreads);
            final CountDownLatch afterInitBlocker = new CountDownLatch(1);
            final CountDownLatch allDone = new CountDownLatch(numThreads);
            for (final Runnable submittedTestRunnable : runnables) {
                threadPool.submit(new Runnable() {
                    public void run() {
                        allExecutorThreadsReady.countDown();
                        try {
                            afterInitBlocker.await();
                            submittedTestRunnable.run();
                        } catch (final Throwable e) {
                            exceptions.add(e);
                        } finally {
                            allDone.countDown();
                        }
                    }
                });
            }
            // wait until all threads are ready
            assertTrue("Timeout initializing threads! Perform long lasting initializations before passing runnables to assertConcurrent", allExecutorThreadsReady.await(runnables.size() * 10, TimeUnit.MILLISECONDS));
            // start all test runners
            afterInitBlocker.countDown();
            assertTrue(message + " timeout! More than" + maxTimeoutSeconds + "seconds", allDone.await(maxTimeoutSeconds, TimeUnit.SECONDS));
        } finally {
            threadPool.shutdownNow();
        }
        assertTrue(message + "failed with exception(s)" + exceptions, exceptions.isEmpty());
    }

    private ProductDetail getStoredProductDetail() {
        OptionItem stored1 = new OptionItem("value1", "-8");
        OptionItem stored2 = new OptionItem("value2", "+5");
        OptionItem stored3 = new OptionItem("value1", "+10");
        ProductOption storedAddOn1 = new ProductOption();
        storedAddOn1.setTitle("option1");
        storedAddOn1.setOptions(new ArrayList<>());
        storedAddOn1.getOptions().add(stored1);
        storedAddOn1.getOptions().add(stored2);
        ProductOption storedAddOn2 = new ProductOption();
        storedAddOn2.setTitle("option2");
        storedAddOn2.setOptions(new ArrayList<>());
        storedAddOn2.getOptions().add(stored3);


        ProductDetail productDetail = new ProductDetail();
        productDetail.setActualStorage(50);
        productDetail.setOrderStorage(50);
        productDetail.setPrice(BigDecimal.valueOf(100));
        productDetail.setSelectedOptions(new ArrayList<>());

        productDetail.getSelectedOptions().add(storedAddOn1);
        productDetail.getSelectedOptions().add(storedAddOn2);
        return productDetail;
    }
}
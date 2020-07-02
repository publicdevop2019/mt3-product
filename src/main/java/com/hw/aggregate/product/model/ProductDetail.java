package com.hw.aggregate.product.model;

import com.hw.aggregate.product.ProductApplicationService;
import com.hw.aggregate.product.ProductDetailRepo;
import com.hw.aggregate.product.command.*;
import com.hw.aggregate.product.exception.ProductNotFoundException;
import com.hw.aggregate.product.exception.SkuAlreadyExistException;
import com.hw.aggregate.product.exception.SkuNotExistException;
import com.hw.shared.Auditable;
import com.hw.shared.StringSetConverter;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.hw.config.AppConstant.ADMIN_ADJUST;

@Data
@Entity
@Table
@NoArgsConstructor
public class ProductDetail extends Auditable {

    @Id
    private Long id;

    private String imageUrlSmall;

    private String name;

    private String description;

    @Convert(converter = ProductStatus.DBConverter.class)
    private ProductStatus status;

    @Column(length = 10000)
    @Convert(converter = ProductOptionConverter.class)
    private List<ProductOption> selectedOptions;

    @Convert(converter = StringSetConverter.class)
    private Set<String> imageUrlLarge;

    @Convert(converter = StringSetConverter.class)
    private Set<String> specification;

    @Convert(converter = StringSetConverter.class)
    private Set<String> attrKey;

    @Convert(converter = StringSetConverter.class)
    private Set<String> attrProd;

    @Convert(converter = StringSetConverter.class)
    private Set<String> attrGen;

    @ElementCollection
    @CollectionTable(name = "product_sku_map", joinColumns = @JoinColumn(name = "product_id"), uniqueConstraints = @UniqueConstraint(columnNames = {"attributesSales", "product_id"}))
    private List<ProductSku> productSkuList;

    public ProductDetail(Long id, String name, String attributes) {
        this.id = id;
        this.name = name;
        this.attrKey = new HashSet<>(Arrays.asList(attributes.split(",")));
    }

    public static ProductDetail create(Long id, CreateProductAdminCommand command, ProductDetailRepo repo) {
        ProductDetail productDetail = new ProductDetail(id, command);
        return repo.save(productDetail);
    }

    public static ProductDetail read(Long id, ProductDetailRepo repo) {
        Optional<ProductDetail> findById = repo.findById(id);
        if (findById.isEmpty())
            throw new ProductNotFoundException();
        return findById.get();
    }

    public void update(UpdateProductAdminCommand command, ProductApplicationService productApplicationService) {
        this.imageUrlSmall = command.getImageUrlSmall();
        this.name = command.getName();
        this.description = command.getDescription();
        this.selectedOptions = command.getSelectedOptions();
        this.imageUrlLarge = command.getImageUrlLarge();
        this.specification = command.getSpecification();
        this.attrKey = command.getAttributesKey();
        this.attrProd = command.getAttributesProd();
        this.attrGen = command.getAttributesGen();
        this.status = command.getStatus();
        command.getSkus().forEach(e -> {
            if (e.getSales() == null)
                e.setSales(0);
            e.setAttributesSales(new TreeSet(e.getAttributesSales()));
        });
        adjustSku(command.getSkus(), productApplicationService);
    }

    private void adjustSku(List<UpdateProductAdminSkuCommand> commands, ProductApplicationService productApplicationService) {
        commands.forEach(command -> {
            if (command.getStorageActual() != null && command.getStorageOrder() != null) {
                // new sku
                boolean b = this.productSkuList.stream().anyMatch(e -> e.getAttributesSales().equals(command.getAttributesSales()));
                if (b)
                    throw new SkuAlreadyExistException();
                ProductSku productSku = new ProductSku();
                productSku.setSales(command.getSales() == null ? 0 : command.getSales());
                productSku.setStorageActual(command.getStorageActual());
                productSku.setStorageOrder(command.getStorageOrder());
                productSku.setAttributesSales(command.getAttributesSales());
                productSku.setPrice(command.getPrice());
                this.productSkuList.add(productSku);
            } else {
                //existing sku
                Optional<ProductSku> first = this.productSkuList.stream().filter(e -> e.getAttributesSales().equals(command.getAttributesSales())).findFirst();
                if (first.isEmpty())
                    throw new SkuNotExistException();
                //update price
                ProductSku productSku = first.get();
                productSku.setPrice(command.getPrice());
                if (command.getDecreaseOrderStorage() != null) {
                    DecreaseOrderStorageCommand command1 = new DecreaseOrderStorageCommand();
                    command1.setTxId(UUID.randomUUID().toString() + ADMIN_ADJUST);
                    command1.setChangeList(getStorageChangeDetail(command, command.getDecreaseOrderStorage()));
                    productApplicationService.decreaseOrderStorageForMappedProducts(command1);
                }
                if (command.getDecreaseActualStorage() != null) {
                    DecreaseActualStorageCommand command1 = new DecreaseActualStorageCommand();
                    command1.setTxId(UUID.randomUUID().toString() + ADMIN_ADJUST);
                    command1.setChangeList(getStorageChangeDetail(command, command.getDecreaseActualStorage()));
                    productApplicationService.decreaseActualStorageForMappedProductsAdmin(command1);
                }
                if (command.getIncreaseOrderStorage() != null) {
                    IncreaseOrderStorageCommand command1 = new IncreaseOrderStorageCommand();
                    command1.setTxId(UUID.randomUUID().toString() + ADMIN_ADJUST);
                    command1.setChangeList(getStorageChangeDetail(command, command.getIncreaseOrderStorage()));
                    productApplicationService.increaseOrderStorageForMappedProducts(command1);
                }
                if (command.getIncreaseActualStorage() != null) {
                    IncreaseActualStorageCommand command1 = new IncreaseActualStorageCommand();
                    command1.setTxId(UUID.randomUUID().toString() + ADMIN_ADJUST);
                    command1.setChangeList(getStorageChangeDetail(command, command.getIncreaseActualStorage()));
                    productApplicationService.increaseActualStorageForMappedProductsAdmin(command1);
                }

            }
        });
        // find skus not in update command & remove
        List<ProductSku> collect = this.productSkuList.stream().filter(e -> commands.stream().noneMatch(command -> command.getAttributesSales().equals(e.getAttributesSales()))).collect(Collectors.toList());
        this.productSkuList.removeAll(collect);
    }

    private List<StorageChangeDetail> getStorageChangeDetail(UpdateProductAdminSkuCommand command, Integer increaseOrderStorage) {
        ArrayList<StorageChangeDetail> objects = new ArrayList<>(1);
        StorageChangeDetail storageChangeDetail = new StorageChangeDetail();
        storageChangeDetail.setAmount(increaseOrderStorage);
        storageChangeDetail.setProductId(this.id);
        storageChangeDetail.setAttributeSales(command.getAttributesSales());
        objects.add(storageChangeDetail);
        return objects;
    }

    public static void delete(Long id, ProductDetailRepo repo) {
        ProductDetail read = read(id, repo);
        repo.delete(read);
    }

    private ProductDetail(Long id, CreateProductAdminCommand command) {
        this.id = id;
        this.imageUrlSmall = command.getImageUrlSmall();
        this.name = command.getName();
        this.description = command.getDescription();
        this.selectedOptions = command.getSelectedOptions();
        this.imageUrlLarge = command.getImageUrlLarge();
        this.specification = command.getSpecification();
        this.attrKey = command.getAttributesKey();
        this.attrProd = command.getAttributesProd();
        this.attrGen = command.getAttributesGen();
        this.status = command.getStatus();
        command.getSkus().forEach(e -> {
            if (e.getSales() == null)
                e.setSales(0);
            e.setAttributesSales(new TreeSet(e.getAttributesSales()));
        });
        this.productSkuList = command.getSkus();
    }
}

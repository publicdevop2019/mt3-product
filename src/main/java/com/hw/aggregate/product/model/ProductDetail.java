package com.hw.aggregate.product.model;

import com.hw.aggregate.product.ProductApplicationService;
import com.hw.aggregate.product.ProductDetailRepo;
import com.hw.aggregate.product.command.*;
import com.hw.aggregate.product.exception.ProductNotAvailableException;
import com.hw.aggregate.product.exception.ProductNotFoundException;
import com.hw.aggregate.product.exception.SkuAlreadyExistException;
import com.hw.aggregate.product.exception.SkuNotExistException;
import com.hw.shared.Auditable;
import com.hw.shared.StringSetConverter;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
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

    private Long endAt;

    private Long startAt;

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

    @Convert(converter = StringSetConverter.class)
    private Set<String> attrSalesTotal;

    @ElementCollection
    @CollectionTable(name = "product_sku_map", joinColumns = @JoinColumn(name = "product_id"), uniqueConstraints = @UniqueConstraint(columnNames = {"attributesSales", "product_id"}))
    private List<ProductSku> productSkuList;

    @Column(length = 10000)
    private ArrayList<ProductAttrSaleImages> attributeSaleImages;

    private Integer storageOrder;
    private Integer storageActual;
    private BigDecimal price;
    private Integer sales;


    public ProductDetail(Long id, String name, String attributes, String imageUrlSmall) {
        this.id = id;
        this.name = name;
        this.attrKey = new HashSet<>(Arrays.asList(attributes.split(",")));
        this.imageUrlSmall = imageUrlSmall;
    }

    public static ProductDetail create(Long id, CreateProductAdminCommand command, ProductDetailRepo repo) {
        ProductDetail productDetail = new ProductDetail(id, command);
        return repo.save(productDetail);
    }

    public static ProductDetail readAdmin(Long id, ProductDetailRepo repo) {
        Optional<ProductDetail> findById = repo.findById(id);
        if (findById.isEmpty())
            throw new ProductNotFoundException();
        return findById.get();
    }

    public static ProductDetail readCustomer(Long id, ProductDetailRepo repo) {
        Optional<ProductDetail> findById = repo.findById(id);
        if (findById.isEmpty())
            throw new ProductNotFoundException();
        if (!ProductDetail.isAvailable(findById.get()))
            throw new ProductNotAvailableException();
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
        this.startAt = command.getStartAt();
        this.endAt = command.getEndAt();
        if (command.getSkus() != null) {
            command.getSkus().forEach(e -> {
                if (e.getSales() == null)
                    e.setSales(0);
                e.setAttributesSales(new TreeSet<>(e.getAttributesSales()));
            });
            adjustSku(command.getSkus(), productApplicationService);
            this.attrSalesTotal = command.getSkus().stream().map(UpdateProductAdminCommand.UpdateProductAdminSkuCommand::getAttributesSales).flatMap(Collection::stream).collect(Collectors.toSet());
            this.attributeSaleImages = command.getAttributeSaleImages().stream().map(e ->
                    {
                        ProductAttrSaleImages productAttrSaleImages = new ProductAttrSaleImages();
                        productAttrSaleImages.setAttributeSales(e.getAttributeSales());
                        productAttrSaleImages.setImageUrls(e.getImageUrls());
                        return productAttrSaleImages;
                    }
            ).collect(Collectors.toCollection(ArrayList::new));
        } else {
            this.productSkuList = null;
            this.price = command.getPrice();
            if (command.getDecreaseOrderStorage() != null) {
                DecreaseOrderStorageCommand command1 = new DecreaseOrderStorageCommand();
                command1.setTxId(UUID.randomUUID().toString() + ADMIN_ADJUST);
                command1.setChangeList(getStorageChangeDetail(command.getDecreaseOrderStorage()));
                productApplicationService.decreaseOrderStorageForMappedProducts(command1);
            }
            if (command.getDecreaseActualStorage() != null) {
                DecreaseActualStorageCommand command1 = new DecreaseActualStorageCommand();
                command1.setTxId(UUID.randomUUID().toString() + ADMIN_ADJUST);
                command1.setChangeList(getStorageChangeDetail(command.getDecreaseActualStorage()));
                productApplicationService.decreaseActualStorageForMappedProductsAdmin(command1);
            }
            if (command.getIncreaseOrderStorage() != null) {
                IncreaseOrderStorageCommand command1 = new IncreaseOrderStorageCommand();
                command1.setTxId(UUID.randomUUID().toString() + ADMIN_ADJUST);
                command1.setChangeList(getStorageChangeDetail(command.getIncreaseOrderStorage()));
                productApplicationService.increaseOrderStorageForMappedProducts(command1);
            }
            if (command.getIncreaseActualStorage() != null) {
                IncreaseActualStorageCommand command1 = new IncreaseActualStorageCommand();
                command1.setTxId(UUID.randomUUID().toString() + ADMIN_ADJUST);
                command1.setChangeList(getStorageChangeDetail(command.getIncreaseActualStorage()));
                productApplicationService.increaseActualStorageForMappedProductsAdmin(command1);
            }
        }
    }

    public void updateStatus(ProductStatus status, ProductDetailRepo repo) {
        Long current = new Date().getTime();
        if (ProductStatus.AVAILABLE.equals(status)) {
            //make product available
            if (this.startAt != null && this.endAt != null) {
                if (this.startAt.compareTo(current) <= 0 && this.endAt.compareTo(current) > 0) {
                    //do nothing, product is already available
                } else {
                    if (this.startAt.compareTo(current) > 0) {
                        this.startAt = new Date().getTime();
                    } else {
                        //this.endAt.compareTo(current) <= 0
                        //set endAt to null, user need to manual update endAt
                        this.endAt = null;
                    }
                }
            } else if (this.startAt != null && this.endAt == null) {
                if (this.startAt.compareTo(current) >= 0) {
                    this.startAt = current;
                } else {
                    //do nothing
                }
            } else if (this.startAt == null && this.endAt == null) {
                this.startAt = current;
            } else if (this.startAt == null && this.endAt != null) {
                this.startAt = current;
                if (this.endAt.compareTo(current) > 0) {
                    //do nothing
                } else {
                    this.endAt = null;
                }
            }
        } else {
            //make product unavailable
            if (this.startAt != null && this.endAt != null) {
                if (this.startAt.compareTo(current) <= 0 && this.endAt.compareTo(current) > 0) {
                    this.endAt = current;
                } else {
                    if (this.startAt.compareTo(current) > 0) {
                        this.startAt = null;
                    } else {
                        //do nothing
                    }
                }
            } else if (this.startAt != null && this.endAt == null) {
                this.startAt = null;
            } else if (this.startAt == null && this.endAt == null) {
                //do nothing
            } else if (this.startAt == null && this.endAt != null) {
                //do nothing
            }
        }
        repo.save(this);
    }

    public static boolean isAvailable(ProductDetail productDetail) {
        Long current = new Date().getTime();
        if (productDetail.getStartAt() == null)
            return false;
        if (current.compareTo(productDetail.getStartAt()) < 0) {
            return false;
        } else {
            if (productDetail.getEndAt() == null) {
                return true;
            } else if (current.compareTo(productDetail.getEndAt()) < 0) {
                return true;
            } else {
                return false;
            }
        }
    }

    private void adjustSku(List<UpdateProductAdminCommand.UpdateProductAdminSkuCommand> commands, ProductApplicationService productApplicationService) {
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
                updateStorage(productApplicationService, command);

            }
        });
        // find skus not in update command & remove
        List<ProductSku> collect = this.productSkuList.stream().filter(e -> commands.stream().noneMatch(command -> command.getAttributesSales().equals(e.getAttributesSales()))).collect(Collectors.toList());
        this.productSkuList.removeAll(collect);
    }

    private void updateStorage(ProductApplicationService productApplicationService, UpdateProductAdminCommand.UpdateProductAdminSkuCommand command) {
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

    private List<StorageChangeDetail> getStorageChangeDetail(UpdateProductAdminCommand.UpdateProductAdminSkuCommand command, Integer increaseOrderStorage) {
        ArrayList<StorageChangeDetail> objects = new ArrayList<>(1);
        StorageChangeDetail storageChangeDetail = new StorageChangeDetail();
        storageChangeDetail.setAmount(increaseOrderStorage);
        storageChangeDetail.setProductId(this.id);
        storageChangeDetail.setAttributeSales(command.getAttributesSales());
        objects.add(storageChangeDetail);
        return objects;
    }

    private List<StorageChangeDetail> getStorageChangeDetail(Integer increaseOrderStorage) {
        ArrayList<StorageChangeDetail> objects = new ArrayList<>(1);
        StorageChangeDetail storageChangeDetail = new StorageChangeDetail();
        storageChangeDetail.setAmount(increaseOrderStorage);
        storageChangeDetail.setProductId(this.id);
        objects.add(storageChangeDetail);
        return objects;
    }

    public static void delete(Long id, ProductDetailRepo repo) {
        ProductDetail read = readAdmin(id, repo);
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
        this.startAt = (command.getStartAt());
        this.endAt = (command.getEndAt());
        if (command.getSkus() != null) {
            command.getSkus().forEach(e -> {
                if (e.getSales() == null)
                    e.setSales(0);
                e.setAttributesSales(e.getAttributesSales());
            });
            this.attrSalesTotal = command.getSkus().stream().map(CreateProductAdminCommand.CreateProductSkuAdminCommand::getAttributesSales).flatMap(Collection::stream).collect(Collectors.toSet());
            this.productSkuList = command.getSkus().stream().map(e -> {
                ProductSku productSku = new ProductSku();
                productSku.setPrice(e.getPrice());
                productSku.setAttributesSales(e.getAttributesSales());
                productSku.setStorageOrder(e.getStorageOrder());
                productSku.setStorageActual(e.getStorageActual());
                productSku.setSales(e.getSales());
                return productSku;
            }).collect(Collectors.toList());
            this.attributeSaleImages = command.getAttributeSaleImages().stream().map(e ->
                    {
                        ProductAttrSaleImages productAttrSaleImages = new ProductAttrSaleImages();
                        productAttrSaleImages.setAttributeSales(e.getAttributeSales());
                        productAttrSaleImages.setImageUrls(e.getImageUrls());
                        return productAttrSaleImages;
                    }
            ).collect(Collectors.toCollection(ArrayList::new));
        } else {
            this.storageOrder = command.getStorageOrder();
            this.storageActual = command.getStorageActual();
            this.sales = command.getSales();
            this.price = command.getPrice();
        }
    }

}

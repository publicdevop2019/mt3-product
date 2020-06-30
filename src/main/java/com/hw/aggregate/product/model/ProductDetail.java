package com.hw.aggregate.product.model;

import com.hw.aggregate.product.ProductDetailRepo;
import com.hw.aggregate.product.command.CreateProductAdminCommand;
import com.hw.aggregate.product.command.UpdateProductAdminCommand;
import com.hw.aggregate.product.exception.ProductNotFoundException;
import com.hw.shared.Auditable;
import com.hw.shared.StringSetConverter;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.*;

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

    public void update(UpdateProductAdminCommand command) {
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

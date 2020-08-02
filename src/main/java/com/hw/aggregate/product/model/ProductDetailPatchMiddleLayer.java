package com.hw.aggregate.product.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.hw.aggregate.product.ProductDetailRepo;
import com.hw.aggregate.product.exception.DataPatchException;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @description this class defines what filed can be patched
 */
@Data
public class ProductDetailPatchMiddleLayer {
    public ProductDetailPatchMiddleLayer() {
    }

    private String name;

    private String imageUrlSmall;

    private Set<String> imageUrlLarge;

    private String description;

    private Long startAt;

    private Long endAt;

    private Set<String> specification;

    private List<ProductOption> selectedOptions;

    private Set<String> attributesKey;

    private Set<String> attributesProd;

    private Set<String> attributesGen;

    private List<ProductSkuAdminPatchRepresentation> productSkuList;

    private List<ProductAttrSaleImagesAdminPatchRepresentation> attributeSaleImages;

    private BigDecimal lowestPrice;

    public ProductDetailPatchMiddleLayer(ProductDetail productDetail) {
        this.name = productDetail.getName();
        this.imageUrlSmall = productDetail.getImageUrlSmall();
        this.description = productDetail.getDescription();
        this.startAt = productDetail.getStartAt();
        this.endAt = productDetail.getEndAt();
        this.selectedOptions = productDetail.getSelectedOptions();
        this.imageUrlLarge = productDetail.getImageUrlLarge();
        this.specification = productDetail.getSpecification();
        this.attributesKey = productDetail.getAttrKey();
        this.attributesProd = productDetail.getAttrProd();
        this.attributesGen = productDetail.getAttrGen();

        if (productDetail.getProductSkuList() != null && productDetail.getProductSkuList().size() != 0) {
            this.productSkuList = productDetail.getProductSkuList().stream().map(ProductSkuAdminPatchRepresentation::new).collect(Collectors.toList());
        } else {
            this.lowestPrice = productDetail.getLowestPrice();
        }
        if (productDetail.getAttributeSaleImages() != null)
            this.attributeSaleImages = productDetail.getAttributeSaleImages().stream().map(ProductAttrSaleImagesAdminPatchRepresentation::new).collect(Collectors.toList());
    }

    @Data
    public static class ProductSkuAdminPatchRepresentation {
        public ProductSkuAdminPatchRepresentation() {
        }

        private Set<String> attributesSales;
        private BigDecimal price;
        private Integer sales;

        public ProductSkuAdminPatchRepresentation(ProductSku sku) {
            this.attributesSales = sku.getAttributesSales();
            this.price = sku.getPrice();
            this.sales = sku.getSales();
        }
    }

    @Data
    public static class ProductAttrSaleImagesAdminPatchRepresentation {
        public ProductAttrSaleImagesAdminPatchRepresentation() {
        }

        private String attributeSales;
        private List<String> imageUrls;

        public ProductAttrSaleImagesAdminPatchRepresentation(ProductAttrSaleImages productAttrSaleImages) {
            this.attributeSales = productAttrSaleImages.getAttributeSales();
            this.imageUrls = productAttrSaleImages.getImageUrls();
        }
    }

    public static ProductDetail doPatch(JsonPatch patch, ProductDetail original, ObjectMapper om, ProductDetailRepo repo) {
        ProductDetailPatchMiddleLayer command = new ProductDetailPatchMiddleLayer(original);
        ProductDetailPatchMiddleLayer patchMiddleLayer;
        try {
            JsonNode jsonNode = om.convertValue(command, JsonNode.class);
            JsonNode patchedNode = patch.apply(jsonNode);
            patchMiddleLayer = om.treeToValue(patchedNode, ProductDetailPatchMiddleLayer.class);
        } catch (JsonPatchException | JsonProcessingException e) {
            e.printStackTrace();
            throw new DataPatchException();
        }
        List<ProductDetailPatchMiddleLayer.ProductAttrSaleImagesAdminPatchRepresentation> imagesAdminPatchRepresentation = patchMiddleLayer.getAttributeSaleImages();
        ArrayList<ProductAttrSaleImages> updatedSalesImages = original.getAttributeSaleImages();
        int size = imagesAdminPatchRepresentation != null ? imagesAdminPatchRepresentation.size() : 0;
        IntStream.range(0, size).forEach(index -> {
            BeanUtils.copyProperties(imagesAdminPatchRepresentation.get(index), updatedSalesImages.get(index));
        });
        List<ProductDetailPatchMiddleLayer.ProductSkuAdminPatchRepresentation> productSkuList = patchMiddleLayer.getProductSkuList();
        List<ProductSku> updatedSkus = original.getProductSkuList();
        int size2 = productSkuList != null ? productSkuList.size() : 0;
        IntStream.range(0, size2).forEach(index -> {
            BeanUtils.copyProperties(productSkuList.get(index), updatedSkus.get(index));
        });
        BeanUtils.copyProperties(patchMiddleLayer, original);
        original.setAttributeSaleImages(updatedSalesImages);
        original.setProductSkuList(updatedSkus);
        original.setAttrGen(patchMiddleLayer.getAttributesGen());
        original.setAttrKey(patchMiddleLayer.getAttributesKey());
        original.setAttrProd(patchMiddleLayer.getAttributesProd());
        return repo.save(original);
    }
}

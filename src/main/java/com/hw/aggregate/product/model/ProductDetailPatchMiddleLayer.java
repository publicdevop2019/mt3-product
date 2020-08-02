package com.hw.aggregate.product.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.hw.aggregate.product.ProductDetailRepo;
import com.hw.aggregate.product.exception.ProductDetailPatchException;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * @description this class defines what filed can be patched
 */
@Data
public class ProductDetailPatchMiddleLayer {
    public ProductDetailPatchMiddleLayer() {
    }

    private Long startAt;

    private Long endAt;

    public ProductDetailPatchMiddleLayer(ProductDetail productDetail) {
        this.startAt = productDetail.getStartAt();
        this.endAt = productDetail.getEndAt();
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
            throw new ProductDetailPatchException();
        }
        BeanUtils.copyProperties(patchMiddleLayer, original);
        return repo.save(original);
    }
}

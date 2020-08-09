package com.hw.aggregate.product.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.hw.aggregate.product.ProductRepo;
import com.hw.aggregate.product.exception.ProductDetailPatchException;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * @description this class defines what filed can be patched
 */
@Data
public class ProductPatchMiddleLayer {
    public ProductPatchMiddleLayer() {
    }

    private Long startAt;

    private Long endAt;
    private String name;

    public ProductPatchMiddleLayer(Product productDetail) {
        this.startAt = productDetail.getStartAt();
        this.endAt = productDetail.getEndAt();
        this.name = productDetail.getName();
    }

    public static Product doPatch(JsonPatch patch, Product original, ObjectMapper om, ProductRepo repo) {
        ProductPatchMiddleLayer command = new ProductPatchMiddleLayer(original);
        ProductPatchMiddleLayer patchMiddleLayer;
        try {
            JsonNode jsonNode = om.convertValue(command, JsonNode.class);
            JsonNode patchedNode = patch.apply(jsonNode);
            patchMiddleLayer = om.treeToValue(patchedNode, ProductPatchMiddleLayer.class);
        } catch (JsonPatchException | JsonProcessingException e) {
            e.printStackTrace();
            throw new ProductDetailPatchException();
        }
        BeanUtils.copyProperties(patchMiddleLayer, original);
        return repo.save(original);
    }
}

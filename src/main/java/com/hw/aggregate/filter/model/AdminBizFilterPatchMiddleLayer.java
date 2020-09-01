package com.hw.aggregate.filter.model;

import com.hw.shared.rest.TypedClass;
import lombok.Data;

import java.util.Set;

@Data
public class AdminBizFilterPatchMiddleLayer extends TypedClass<AdminBizFilterPatchMiddleLayer> {
    private String description;
    private Set<String> catalogs;

    public AdminBizFilterPatchMiddleLayer(BizFilter bizFilter) {
        super(AdminBizFilterPatchMiddleLayer.class);
        this.catalogs = bizFilter.getCatalogs();
        this.description = bizFilter.getDescription();
    }

    public AdminBizFilterPatchMiddleLayer() {
        super(AdminBizFilterPatchMiddleLayer.class);
    }
}

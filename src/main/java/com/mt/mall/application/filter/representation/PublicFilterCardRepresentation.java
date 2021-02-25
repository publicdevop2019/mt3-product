package com.mt.mall.application.filter.representation;

import com.mt.mall.domain.model.filter.FilterItem;
import lombok.Data;

import java.util.Set;

@Data
public class PublicFilterCardRepresentation {
    private String id;
    private String name;
    private Set<String> values;

    public PublicFilterCardRepresentation(FilterItem e) {
        setId(e.getTagId());
        setName(e.getName());
        setValues(e.getValues());
    }
}

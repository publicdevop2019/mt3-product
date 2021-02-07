package com.mt.mall.application.catalog.command;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mt.common.rest.TypedClass;
import com.mt.mall.domain.model.catalog.Catalog;
import com.mt.mall.domain.model.catalog.Type;
import lombok.Data;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class CatalogPatchCommand extends TypedClass<CatalogPatchCommand> {

    private String name;

    private String parentId;
    @JsonDeserialize(as = LinkedHashSet.class)//use linkedHashSet to keep order of elements as it is received
    private Set<String> attributes;

    private Type type;

    public CatalogPatchCommand(Catalog catalog) {
        super(CatalogPatchCommand.class);
        this.name = catalog.getName();
        this.parentId = catalog.getParentId().getDomainId();
        this.attributes = catalog.getAttributes();
        this.type = catalog.getType();
    }

    public CatalogPatchCommand() {
        super(CatalogPatchCommand.class);
    }
}

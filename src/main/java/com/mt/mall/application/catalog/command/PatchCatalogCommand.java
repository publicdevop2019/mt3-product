package com.mt.mall.application.catalog.command;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mt.common.domain.model.restful.TypedClass;
import com.mt.mall.domain.model.catalog.Catalog;
import com.mt.mall.domain.model.catalog.Type;
import lombok.Data;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class PatchCatalogCommand extends TypedClass<PatchCatalogCommand> {

    private String name;

    private String parentId;
    @JsonDeserialize(as = LinkedHashSet.class)//use linkedHashSet to keep order of elements as it is received
    private Set<String> attributes;

    private Type type;

    public PatchCatalogCommand(Catalog catalog) {
        super(PatchCatalogCommand.class);
        this.name = catalog.getName();
        this.parentId = catalog.getParentId().getDomainId();
        this.attributes = catalog.getLinkedTags().stream().map(e -> String.join(":", e.getTagId().getDomainId(), e.getTagValue())).collect(Collectors.toSet());
        this.type = catalog.getType();
    }

    public PatchCatalogCommand() {
        super(PatchCatalogCommand.class);
    }
}

package com.mt.mall.application.filter.command;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.restful.TypedClass;
import com.mt.mall.domain.model.filter.Filter;
import lombok.Data;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class PatchFilterCommand extends TypedClass<PatchFilterCommand> {
    private String description;
    private Set<String> catalogs;

    public PatchFilterCommand(Filter bizFilter) {
        super(PatchFilterCommand.class);
        this.catalogs = bizFilter.getCatalogs().stream().map(DomainId::getDomainId).collect(Collectors.toSet());
        this.description = bizFilter.getDescription();
    }

    public PatchFilterCommand() {
        super(PatchFilterCommand.class);
    }
}

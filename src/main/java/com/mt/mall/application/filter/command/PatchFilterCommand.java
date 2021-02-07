package com.mt.mall.application.filter.command;

import com.mt.common.rest.TypedClass;
import com.mt.mall.domain.model.filter.Filter;
import lombok.Data;

import java.util.Set;

@Data
public class PatchFilterCommand extends TypedClass<PatchFilterCommand> {
    private String description;
    private Set<String> catalogs;

    public PatchFilterCommand(Filter bizFilter) {
        super(PatchFilterCommand.class);
        this.catalogs = bizFilter.getCatalogs();
        this.description = bizFilter.getDescription();
    }

    public PatchFilterCommand() {
        super(PatchFilterCommand.class);
    }
}

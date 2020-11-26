package com.hw.aggregate.filter.representation;

import com.hw.aggregate.filter.model.BizFilter;
import lombok.Data;

import java.util.Set;

@Data
public class AdminBizFilterCardRep {
    private Long id;
    private Set<String> catalogs;
    private String description;
    private Integer version;

    public AdminBizFilterCardRep(BizFilter e) {
        id = e.getId();
        catalogs = e.getCatalogs();
        description=e.getDescription();
        version=e.getVersion();
    }
}

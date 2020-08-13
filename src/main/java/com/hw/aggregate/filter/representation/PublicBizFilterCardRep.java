package com.hw.aggregate.filter.representation;

import com.hw.aggregate.filter.model.BizFilter;
import lombok.Data;

import java.util.Set;

@Data
public class PublicBizFilterCardRep {
    private Long id;
    private String name;
    private Set<String> values;

    public PublicBizFilterCardRep(BizFilter.BizFilterItem e) {
        this.id = e.getId();
        this.name = e.getName();
        this.values = e.getSelectValues();
    }
}

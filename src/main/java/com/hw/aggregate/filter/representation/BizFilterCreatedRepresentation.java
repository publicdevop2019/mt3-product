package com.hw.aggregate.filter.representation;

import com.hw.aggregate.filter.model.BizFilter;
import lombok.Data;

@Data
public class BizFilterCreatedRepresentation {
    private Long id;

    public BizFilterCreatedRepresentation(BizFilter bizFilter) {
        this.id = bizFilter.getId();
    }
}

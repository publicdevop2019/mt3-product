package com.hw.aggregate.filter.representation;

import com.hw.aggregate.filter.model.BizFilter;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Set;

@Data
public class PublicBizFilterCardRep {
    private Long id;
    private String name;
    private Set<String> values;

    public PublicBizFilterCardRep(BizFilter.BizFilterItem e) {
        BeanUtils.copyProperties(e, this);
        this.values = e.getSelectValues();
    }
}

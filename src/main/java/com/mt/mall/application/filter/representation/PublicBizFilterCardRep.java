package com.mt.mall.application.filter.representation;

import com.mt.mall.domain.model.filter.FilterItem;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Set;

@Data
public class PublicBizFilterCardRep {
    private Long id;
    private String name;
    private Set<String> values;

    public PublicBizFilterCardRep(FilterItem e) {
        BeanUtils.copyProperties(e, this);
        this.values = e.getSelectValues();
    }
}

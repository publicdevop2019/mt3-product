package com.mt.mall.application.filter.representation;

import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Set;

@Data
public class AdminBizFilterCardRep {
    private Long id;
    private Set<String> catalogs;
    private String description;
    private Integer version;

    public AdminBizFilterCardRep(Object e) {
        BeanUtils.copyProperties(e, this);
    }
}

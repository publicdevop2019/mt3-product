package com.hw.aggregate.filter.representation;

import com.hw.aggregate.filter.model.BizFilter;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Set;

@Data
public class AdminBizFilterCardRep {
    private Long id;
    private Set<String> catalogs;
    private String description;
    private Integer version;

    public AdminBizFilterCardRep(BizFilter e) {
        BeanUtils.copyProperties(e, this);
    }
}

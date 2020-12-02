package com.hw.aggregate.tag.representation;

import com.hw.aggregate.tag.model.BizTag;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class AppBizTagCardRep {
    private Long id;
    private String name;

    public AppBizTagCardRep(BizTag bizTag) {
        BeanUtils.copyProperties(bizTag, this);
    }
}
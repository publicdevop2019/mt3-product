package com.mt.mall.domain.model.meta;

import com.mt.common.domain.model.restful.SumPagedRep;

public interface MetaRepository {
    SumPagedRep<Meta> metaOfQuery(MetaQuery query);
    void add(Meta meta);
}

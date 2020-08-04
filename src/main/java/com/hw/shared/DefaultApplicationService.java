package com.hw.shared;

import java.util.List;

public abstract class DefaultApplicationService {
    protected  <T> DefaultSumPagedRep<T> select(SelectQueryBuilder<T> queryBuilder, String search, String page, String countFlag, Class<T> clazz) {
        List<T> query = queryBuilder.select(search, page, clazz);
        Long aLong = null;
        if (!"0".equals(countFlag)) {
            aLong = queryBuilder.selectCount(search, clazz);
        }
        return new DefaultSumPagedRep<T>(query, aLong);
    }
}

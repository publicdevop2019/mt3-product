package com.hw.shared;

import lombok.Data;

import java.util.List;

@Data
public class DefaultSumPagedRep<T> implements SumPagedRep<T> {
    private List<T> data;
    private Long totalItemCount;

    public DefaultSumPagedRep(List<T> data, Long aLong) {
        this.data=data;
        this.totalItemCount=aLong;
    }
}

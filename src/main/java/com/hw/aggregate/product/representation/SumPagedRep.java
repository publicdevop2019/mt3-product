package com.hw.aggregate.product.representation;

import java.util.List;

public interface SumPagedRep<T> {

    Long getTotalItemCount();

    List<T> getData();
}

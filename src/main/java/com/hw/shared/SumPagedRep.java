package com.hw.shared;

import java.util.List;

public interface SumPagedRep<T> {

    Long getTotalItemCount();

    List<T> getData();
}

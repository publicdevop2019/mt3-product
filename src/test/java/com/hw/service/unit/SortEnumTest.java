package com.hw.service.unit;

import com.hw.aggregate.product.model.SortCriteriaEnum;
import com.hw.aggregate.product.model.SortOrderEnum;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SortEnumTest {
    @Test
    public void create_enum_asc() {
        SortOrderEnum asc = SortOrderEnum.fromString("asc");
        Assert.assertEquals(SortOrderEnum.ASC, asc);
    }

    @Test
    public void create_enum_desc() {
        SortOrderEnum asc = SortOrderEnum.fromString("desc");
        Assert.assertEquals(SortOrderEnum.DESC, asc);
    }

    @Test
    public void create_enum_name() {
        SortCriteriaEnum name = SortCriteriaEnum.fromString("name");
        Assert.assertEquals(SortCriteriaEnum.NAME, name);
    }

    @Test
    public void create_enum_price() {
        SortCriteriaEnum name = SortCriteriaEnum.fromString("price");
        Assert.assertEquals(SortCriteriaEnum.PRICE, name);
    }
}

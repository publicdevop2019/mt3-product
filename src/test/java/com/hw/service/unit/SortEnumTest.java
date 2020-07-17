package com.hw.service.unit;

import com.hw.aggregate.product.model.SortCriteriaEnum;
import com.hw.aggregate.product.model.SortOrderEnum;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.*;

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

    @Test
    public void collectStringOrderTest() {
        Collection<String> test = new HashSet<>();
        test.add("S");
        test.add("M");
        test.add("L");
        test.add("XL");
        ArrayList<String> strings = new ArrayList<>();
        strings.add("S");
        strings.add("M");
        strings.add("L");
        strings.add("XL");
        TreeSet<String> strings1 = new TreeSet<>();
        strings1.add("S");
        strings1.add("M");
        strings1.add("L");
        strings1.add("XL");
        LinkedHashSet<String> strings2 = new LinkedHashSet<>();
        strings2.add("S");
        strings2.add("M");
        strings2.add("L");
        strings2.add("XL");

    }
}

package com.hw.aggregate.product.model;

import javax.persistence.AttributeConverter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class LongIntegerMapConverter implements AttributeConverter<Map<Long, Integer>, String> {
    @Override
    public String convertToDatabaseColumn(Map<Long, Integer> longIntegerMap) {
        if (longIntegerMap == null)
            return "";
        return longIntegerMap.keySet().stream().map(e -> e.toString() + ":" + longIntegerMap.get(e)).collect(Collectors.joining(","));
    }

    @Override
    public Map<Long, Integer> convertToEntityAttribute(String s) {
        if (s.equals("")) {
            return null;
        }
        HashMap<Long, Integer> stringStringHashMap = new HashMap<>();
        Arrays.stream(s.split(",")).forEach(e -> {
            stringStringHashMap.put(Long.parseLong(e.split(":")[0]), Integer.parseInt(e.split(":")[1]));
        });
        return stringStringHashMap;
    }
}

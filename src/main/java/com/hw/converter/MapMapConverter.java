package com.hw.converter;

import javax.persistence.AttributeConverter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class MapMapConverter implements AttributeConverter<Map<String, Map<String, String>>, String> {
    @Override
    public String convertToDatabaseColumn(Map<String, Map<String, String>> stringMapMap) {
        /**
         *  e.g.
         *  qty:1&1*2&2*3&3,color:white&0.35*black&0.37
         */
        return stringMapMap.keySet().stream().map(e -> e + ":" + stringMapMap.get(e).keySet().stream().map(el -> el + "&" + stringMapMap.get(e).get(el)).collect(Collectors.joining("-"))).collect(Collectors.joining(","));

    }

    @Override
    public Map<String, Map<String, String>> convertToEntityAttribute(String s) {
        Map<String, Map<String, String>> stringStringHashMap = new HashMap<>();
        Arrays.stream(s.split(",")).forEach(e -> {
            Map<String, String> abc = new HashMap<>();
            String qty = e.split(":")[0];
            String detail = e.split(":")[1];
            String[] split = detail.split("-");
            Arrays.stream(split).forEach(el -> {
                String[] split1 = el.split("&");
                abc.put(split1[0], split1[1]);
            });
            stringStringHashMap.put(qty, abc);
        });
        return stringStringHashMap;
    }
}

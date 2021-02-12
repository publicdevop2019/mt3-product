package com.mt.mall.domain.model.filter;

import com.mt.common.domain.model.CommonDomainRegistry;
import lombok.Data;

import javax.persistence.AttributeConverter;
import java.io.Serializable;
import java.util.Set;

@Data
public class FilterItem implements Serializable {
    private static final long serialVersionUID = 1;
    private String id;
    private String name;
    private Set<String> selectValues;

    public FilterItem(String id, String name, Set<String> selectValues) {
        setId(id);
        setName(name);
        setSelectValues(selectValues);
    }

    public static class FilterItemConverter implements AttributeConverter<Set<FilterItem>, byte[]> {
        @Override
        public byte[] convertToDatabaseColumn(Set<FilterItem> redirectURLS) {
            return CommonDomainRegistry.customObjectSerializer().nativeSerialize(redirectURLS);
        }

        @Override
        public Set<FilterItem> convertToEntityAttribute(byte[] bytes) {
            return (Set<FilterItem>) CommonDomainRegistry.customObjectSerializer().nativeDeserialize(bytes);
        }
    }
}

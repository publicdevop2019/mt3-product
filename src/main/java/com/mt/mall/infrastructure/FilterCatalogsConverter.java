package com.mt.mall.infrastructure;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.mall.domain.model.catalog.CatalogId;

import javax.persistence.AttributeConverter;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class FilterCatalogsConverter implements AttributeConverter<Set<CatalogId>, String> {
    @Override
    public String convertToDatabaseColumn(Set<CatalogId> attribute) {
        return String.join(",", attribute.stream().map(DomainId::getDomainId).collect(Collectors.toSet()));
    }

    @Override
    public Set<CatalogId> convertToEntityAttribute(String dbData) {
        if (dbData == null)
            return null;
        return Arrays.stream(dbData.split(",")).map(CatalogId::new).collect(Collectors.toSet());
    }
}

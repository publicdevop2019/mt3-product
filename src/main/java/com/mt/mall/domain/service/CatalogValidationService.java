package com.mt.mall.domain.service;

import com.mt.common.query.QueryUtility;
import com.mt.common.validate.ValidationNotificationHandler;
import com.mt.mall.domain.DomainRegistry;
import com.mt.mall.domain.model.tag.Tag;
import com.mt.mall.domain.model.tag.TagQuery;
import com.mt.mall.domain.model.tag.TagValueType;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class CatalogValidationService {
    public void validate(Set<String> tags, ValidationNotificationHandler handler) {
        Map<String, String> stringStringHashMap = new HashMap<>();
        tags.forEach(e -> {
            String[] split = e.split(":");
            stringStringHashMap.put(split[0], split[1]);
        });
        String queryParam = String.join(".", stringStringHashMap.keySet());
        Set<Tag> tagSet = QueryUtility.getAllByQuery((query, page) -> DomainRegistry.tagRepository().tagsOfQuery(query, page), new TagQuery(queryParam));
        stringStringHashMap.forEach((k, v) -> {
            Optional<Tag> first = tagSet.stream().filter(e -> e.getTagId().getDomainId().equals(k)).findFirst();
            if (first.isEmpty()) {
                handler.handleError("specified tag not found");
            }
            Tag tag = first.get();
            if (!TagValueType.MANUAL.equals(tag.getMethod())) {
                if (!tag.getSelectValues().contains(v))
                    handler.handleError("specified tag value not found");
            }
        });
    }
}

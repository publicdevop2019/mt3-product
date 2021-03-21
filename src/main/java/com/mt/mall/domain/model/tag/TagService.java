package com.mt.mall.domain.model.tag;

import com.mt.mall.domain.DomainRegistry;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class TagService {

    public TagId create(TagId tagId, String name, String description, TagValueType method, Set<String> catalogs, Type type) {
        Tag tag = new Tag(tagId, name, description, method, catalogs, type);
        DomainRegistry.getTagRepository().add(tag);
        return tag.getTagId();
    }
}

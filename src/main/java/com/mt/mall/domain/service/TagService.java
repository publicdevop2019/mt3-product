package com.mt.mall.domain.service;

import com.mt.mall.domain.DomainRegistry;
import com.mt.mall.domain.model.tag.Tag;
import com.mt.mall.domain.model.tag.TagId;
import com.mt.mall.domain.model.tag.TagValueType;
import com.mt.mall.domain.model.tag.Type;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class TagService {

    public TagId create(TagId tagId, String name, String description, TagValueType method, Set<String> catalogs, Type type) {
        Tag tag = new Tag(tagId, name, description, method, catalogs, type);
        DomainRegistry.tagRepository().add(tag);
        return tag.getTagId();
    }
}

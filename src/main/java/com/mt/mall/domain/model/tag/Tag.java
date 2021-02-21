package com.mt.mall.domain.model.tag;

import com.mt.common.audit.Auditable;
import com.mt.common.domain.model.CommonDomainRegistry;
import com.mt.common.persistence.StringSetConverter;
import com.mt.common.validate.HttpValidationNotificationHandler;
import com.mt.common.validate.Validator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Entity
@Table(name = "tag_")
@NoArgsConstructor
public class Tag extends Auditable {
    @Id
    @Setter(AccessLevel.PRIVATE)
    private Long id;

    private String name;

    private String description;

    @Convert(converter = TagValueType.DBConverter.class)
    @Setter(AccessLevel.PRIVATE)
    private TagValueType method;

    @Convert(converter = StringSetConverter.class)
    private Set<String> selectValues;

    @Convert(converter = Type.DBConverter.class)
    @Setter(AccessLevel.PRIVATE)
    private Type type;

    @Embedded
    @Setter(AccessLevel.PRIVATE)
    @AttributeOverrides({
            @AttributeOverride(name = "domainId", column = @Column(name = "tagId", updatable = false, nullable = false))
    })
    private TagId tagId;

    @Version
    @Setter(AccessLevel.NONE)
    private Integer version;

    private void setName(String name) {
        Validator.whitelistOnly(name);
        Validator.lengthLessThanOrEqualTo(name, 50);
        Validator.notBlank(name);
        this.name = name;
    }

    private void setDescription(String description) {
        Validator.whitelistOnly(description);
        Validator.lengthLessThanOrEqualTo(description, 50);
        Validator.notBlank(description);
        this.description = description;
    }

    private void setSelectValues(Set<String> selectValues) {
        Validator.notEmpty(selectValues);
        this.selectValues = selectValues;
    }

    public Tag(TagId tagId, String name, String description, TagValueType valueType, Set<String> selectValues, Type type) {
        setId(CommonDomainRegistry.uniqueIdGeneratorService().id());
        setTagId(tagId);
        setDescription(description);
        setName(name);
        setType(type);
        setMethod(valueType);
        setSelectValues(selectValues);
        new TagValidator(this, new HttpValidationNotificationHandler()).validate();
    }

    public void replace(String name, String description, TagValueType valueType, Set<String> selectValues, Type type) {
        setName(name);
        setDescription(description);
        setMethod(valueType);
        setSelectValues(selectValues);
        setType(type);
        new TagValidator(this, new HttpValidationNotificationHandler()).validate();
    }

}

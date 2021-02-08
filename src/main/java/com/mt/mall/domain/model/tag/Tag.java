package com.mt.mall.domain.model.tag;

import com.mt.common.audit.Auditable;
import com.mt.common.domain.model.CommonDomainRegistry;
import com.mt.common.persistence.StringSetConverter;
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
    public transient static final String ID_LITERAL = "id";
    public transient static final String NAME_LITERAL = "name";
    public transient static final String DESCRIPTION_LITERAL = "description";
    public transient static final String METHOD_LITERAL = "method";
    public transient static final String SELECTED_VALUES_LITERAL = "selectValues";
    @Id
    @Setter(AccessLevel.PRIVATE)
    private Long id;

    @Setter(AccessLevel.PRIVATE)
    private String name;

    @Setter(AccessLevel.PRIVATE)
    private String description;

    @Convert(converter = TagValueType.DBConverter.class)
    @Setter(AccessLevel.PRIVATE)
    private TagValueType method;

    @Convert(converter = StringSetConverter.class)
    @Setter(AccessLevel.PRIVATE)
    private Set<String> selectValues;

    @Convert(converter = Type.DBConverter.class)
    @Setter(AccessLevel.PRIVATE)
    private Type type;

    @Setter(AccessLevel.PRIVATE)
    private TagId tagId;
    public transient static final String TYPE_LITERAL = "type";
    @Version
    @Setter(AccessLevel.NONE)
    private Integer version;

    public Tag(TagId tagId, String name, String description, TagValueType valueType, Set<String> selectValues, Type type) {
        setId(CommonDomainRegistry.uniqueIdGeneratorService().id());
        setTagId(tagId);
        setDescription(description);
        setName(name);
        setType(type);
        setMethod(valueType);
        setSelectValues(selectValues);
    }

    public void replace(String name, String description, TagValueType valueType, Set<String> selectValues, Type type) {
        setName(name);
        setDescription(description);
        setMethod(valueType);
        setSelectValues(selectValues);
        setType(type);
    }

}

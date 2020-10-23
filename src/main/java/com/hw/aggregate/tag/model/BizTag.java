package com.hw.aggregate.tag.model;

import com.hw.aggregate.tag.command.CreateBizTagCommand;
import com.hw.aggregate.tag.command.UpdateBizTagCommand;
import com.hw.shared.Auditable;
import com.hw.shared.EnumDBConverter;
import com.hw.shared.LinkedHashSetConverter;
import com.hw.shared.rest.IdBasedEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Set;

@Data
@Entity
@Table(name = "biz_attribute")
@NoArgsConstructor
public class BizTag extends Auditable implements IdBasedEntity {
    @Id
    private Long id;
    public transient static final String ID_LITERAL = "id";

    private String name;
    public transient static final String NAME_LITERAL = "name";

    private String description;
    public transient static final String DESCRIPTION_LITERAL = "description";

    @Convert(converter = AttributeMethod.DBConverter.class)
    private AttributeMethod method;
    public transient static final String METHOD_LITERAL = "method";

    @Convert(converter = LinkedHashSetConverter.class)
    private Set<String> selectValues;
    public transient static final String SELECTED_VALUES_LITERAL = "selectValues";

    @Convert(converter = BizAttributeType.DBConverter.class)
    private BizAttributeType type;
    public transient static final String TYPE_LITERAL = "type";

    public static BizTag create(Long id, CreateBizTagCommand command) {
        return new BizTag(id, command);
    }

    private BizTag(Long id, CreateBizTagCommand command) {
        this.id = id;
        this.name = command.getName();
        this.description = command.getDescription();
        this.method = command.getMethod();
        this.selectValues = command.getSelectValues();
        this.type = command.getType();
    }

    public BizTag replace(UpdateBizTagCommand command) {
        this.setName(command.getName());
        this.setDescription(command.getDescription());
        this.setMethod(command.getMethod());
        this.setSelectValues(command.getSelectValues());
        this.setType(command.getType());
        return this;
    }

    public enum AttributeMethod {
        MANUAL,
        SELECT;

        public static class DBConverter extends EnumDBConverter {
            public DBConverter() {
                super(AttributeMethod.class);
            }
        }
    }

    public enum BizAttributeType {
        KEY_ATTR,
        SALES_ATTR,
        PROD_ATTR,
        GEN_ATTR;

        public static class DBConverter extends EnumDBConverter {
            public DBConverter() {
                super(BizAttributeType.class);
            }
        }
    }
}

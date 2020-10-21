package com.hw.aggregate.catalog.model;

import com.hw.aggregate.catalog.command.CreateBizCatalogCommand;
import com.hw.aggregate.catalog.command.UpdateBizCatalogCommand;
import com.hw.shared.Auditable;
import com.hw.shared.EnumDBConverter;
import com.hw.shared.StringSetConverter;
import com.hw.shared.rest.IdBasedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Table(name = "biz_catalog") // had to customize name due to catalog is a db keyword
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BizCatalog extends Auditable implements IdBasedEntity {

    @Id
    private Long id;
    public transient static final String ID_LITERAL = "id";

    @NotBlank
    @Column(nullable = false)
    private String name;
    public transient static final String NAME_LITERAL = "name";

    private Long parentId;
    public transient static final String PARENT_ID_LITERAL = "parentId";

    @Convert(converter = StringSetConverter.class)
    private Set<String> attributes;
    public transient static final String ATTR_LITERAL = "attributes";

    @Convert(converter = CatalogType.DBConverter.class)
    private CatalogType type;
    public transient static final String TYPE_LITERAL = "type";

    public static BizCatalog create(Long id, CreateBizCatalogCommand command) {
        return new BizCatalog(id, command);
    }


    public void replace(UpdateBizCatalogCommand command) {
        this.setName(command.getName());
        this.setParentId(command.getParentId());
        this.setAttributes(command.getAttributes());
        this.setType(command.getCatalogType());
    }


    private BizCatalog(Long id, CreateBizCatalogCommand command) {
        this.id = id;
        this.name = command.getName();
        this.parentId = command.getParentId();
        this.attributes = command.getAttributes();
        this.type = command.getCatalogType();
    }

    public enum CatalogType {
        FRONTEND,
        BACKEND,
        ;

        public static class DBConverter extends EnumDBConverter<CatalogType> {
            public DBConverter() {
                super(CatalogType.class);
            }
        }
    }
}

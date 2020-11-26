package com.hw.aggregate.filter.model;

import com.hw.aggregate.filter.command.AdminCreateBizFilterCommand;
import com.hw.aggregate.filter.command.AdminUpdateBizFilterCommand;
import com.hw.shared.Auditable;
import com.hw.shared.StringSetConverter;
import com.hw.shared.rest.Aggregate;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;

@Data
@Entity
@Table(name = "biz_filter")
@NoArgsConstructor
public class BizFilter extends Auditable implements Aggregate {
    @Id
    private Long id;
    public transient static final String ID_LITERAL = "id";
    @Convert(converter = StringSetConverter.class)
    private Set<String> catalogs;
    public transient static final String ENTITY_CATALOG_LITERAL = "catalogs";
    private String description;
    @Column(length = 10000)
    private ArrayList<BizFilterItem> filterItems;
    @Version
    @Setter(AccessLevel.NONE)
    private Integer version;

    @Data
    public static class BizFilterItem implements Serializable {
        private static final long serialVersionUID = 1;
        private Long id;
        private String name;
        private Set<String> selectValues;
    }

    public static BizFilter create(Long id, AdminCreateBizFilterCommand command) {
        return new BizFilter(id, command);
    }

    public void replace(AdminUpdateBizFilterCommand command) {
        this.catalogs = command.getCatalogs();
        this.filterItems = new ArrayList<>();
        command.getFilters().forEach(e -> {
            BizFilterItem bizFilterItem = new BizFilterItem();
            bizFilterItem.setId(e.getId());
            bizFilterItem.setName(e.getName());
            bizFilterItem.setSelectValues(e.getValues());
            this.filterItems.add(bizFilterItem);
        });
        this.description = command.getDescription();
    }

    private BizFilter(Long id, AdminCreateBizFilterCommand command) {
        this.id = id;
        this.catalogs = command.getCatalogs();
        this.filterItems = new ArrayList<>();
        command.getFilters().forEach(e -> {
            BizFilterItem bizFilterItem = new BizFilterItem();
            bizFilterItem.setId(e.getId());
            bizFilterItem.setName(e.getName());
            bizFilterItem.setSelectValues(e.getValues());
            this.filterItems.add(bizFilterItem);
        });
        this.description = command.getDescription();
    }
}

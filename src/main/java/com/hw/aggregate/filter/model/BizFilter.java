package com.hw.aggregate.filter.model;

import com.hw.aggregate.filter.BizFilterRepository;
import com.hw.aggregate.filter.command.CreateBizFilterCommand;
import com.hw.aggregate.filter.command.UpdateBizFilterCommand;
import com.hw.aggregate.filter.exception.BizFilterNotFoundException;
import com.hw.shared.Auditable;
import com.hw.shared.LinkedHashSetConverter;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

@Data
@Entity
@Table(name = "biz_filter")
@NoArgsConstructor
public class BizFilter extends Auditable {
    @Id
    private Long id;
    @Convert(converter = LinkedHashSetConverter.class)
    private Set<String> linkedCatalog;
    @Column(length = 10000)
    private ArrayList<BizFilterItem> filterItems;

    @Data
    public static class BizFilterItem implements Serializable {
        private static final long serialVersionUID = 1;
        private Long id;
        private String name;
        private Set<String> selectValues;
    }

    public static BizFilter create(Long id, CreateBizFilterCommand command, BizFilterRepository repository) {
        BizFilter bizFilter = new BizFilter(id, command);
        return repository.save(bizFilter);
    }

    public BizFilter(Long id, CreateBizFilterCommand command) {
        this.id = id;
        this.linkedCatalog = command.getCatalogs();
        this.filterItems = new ArrayList<>();
        command.getFilters().forEach(e -> {
            BizFilterItem bizFilterItem = new BizFilterItem();
            bizFilterItem.setId(e.getId());
            bizFilterItem.setName(e.getName());
            bizFilterItem.setSelectValues(e.getValues());
            this.filterItems.add(bizFilterItem);
        });
    }

    public static void update(Long id, UpdateBizFilterCommand command, BizFilterRepository repository) {
        BizFilter read = read(id, repository);
        read.linkedCatalog = command.getCatalogs();
        read.filterItems = new ArrayList<>();
        command.getFilters().forEach(e -> {
            BizFilterItem bizFilterItem = new BizFilterItem();
            bizFilterItem.setId(e.getId());
            bizFilterItem.setName(e.getName());
            bizFilterItem.setSelectValues(e.getValues());
            read.filterItems.add(bizFilterItem);
        });
        repository.save(read);
    }

    public static BizFilter read(Long id, BizFilterRepository repository) {
        Optional<BizFilter> byId = repository.findById(id);
        if (byId.isEmpty())
            throw new BizFilterNotFoundException();
        return byId.get();
    }

    public static void delete(Long id, BizFilterRepository repository) {
        Optional<BizFilter> byId = repository.findById(id);
        if (byId.isEmpty())
            throw new BizFilterNotFoundException();
        repository.deleteById(id);
    }
}

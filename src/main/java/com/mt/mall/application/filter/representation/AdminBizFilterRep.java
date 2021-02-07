package com.mt.mall.application.filter.representation;

import com.mt.mall.domain.model.filter.Filter;
import com.mt.mall.domain.model.filter.FilterItem;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class AdminBizFilterRep {
    private Long id;
    private Set<String> catalogs;
    private List<BizFilterItemAdminRepresentation> filters;
    private String description;
    private Integer version;
    public AdminBizFilterRep(Filter read) {
        BeanUtils.copyProperties(read, this);
        this.filters = read.getFilterItems().stream().map(BizFilterItemAdminRepresentation::new).collect(Collectors.toList());
    }

    @Data
    private static class BizFilterItemAdminRepresentation {
        private Long id;
        private String name;
        private Set<String> values;

        public BizFilterItemAdminRepresentation(FilterItem e) {
            BeanUtils.copyProperties(e, this);
            this.values = e.getSelectValues();
        }
    }
}

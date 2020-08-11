package com.hw.aggregate.filter.representation;

import com.hw.aggregate.filter.model.BizFilter;
import com.hw.shared.sql.SumPagedRep;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class BizFilterPublicRep extends SumPagedRep<BizFilterPublicRep.BizFilterItemCustomerRepresentation> {
    public BizFilterPublicRep(SumPagedRep<BizFilter> select1) {
        List<BizFilter> data = select1.getData();
        if (data.size() != 0)
            this.data = select1.getData().get(0).getFilterItems().stream().map(BizFilterItemCustomerRepresentation::new).collect(Collectors.toList());
    }

    @Data
    public static class BizFilterItemCustomerRepresentation {
        private Long id;
        private String name;
        private Set<String> values;

        public BizFilterItemCustomerRepresentation(BizFilter.BizFilterItem e) {
            this.id = e.getId();
            this.name = e.getName();
            this.values = e.getSelectValues();
        }
    }
}

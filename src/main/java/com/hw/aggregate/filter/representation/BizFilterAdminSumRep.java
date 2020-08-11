package com.hw.aggregate.filter.representation;

import com.hw.aggregate.filter.model.BizFilter;
import com.hw.shared.SumPagedRep;
import lombok.Data;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class BizFilterAdminSumRep extends SumPagedRep<BizFilterAdminSumRep.BizFilterItemCardRepresentation> {

    public BizFilterAdminSumRep(SumPagedRep<BizFilter> select1) {
        this.data = select1.getData().stream().map(BizFilterItemCardRepresentation::new).collect(Collectors.toList());
        this.totalItemCount = select1.getTotalItemCount();
    }

    @Data
    protected static class BizFilterItemCardRepresentation {
        private Long id;
        private Set<String> catalogs;

        public BizFilterItemCardRepresentation(BizFilter e) {
            id = e.getId();
            catalogs = e.getLinkedCatalog();
        }
    }
}

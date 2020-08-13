package com.hw.aggregate.attribute.representation;

import com.hw.aggregate.attribute.model.BizAttribute;
import com.hw.shared.sql.SumPagedRep;
import lombok.Data;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class BizAttributeAppSumRep extends SumPagedRep<BizAttributeAppSumRep.BizAttributeCardRepresentation> {
    public BizAttributeAppSumRep(SumPagedRep<BizAttribute> select) {
        this.data = select.getData().stream().map(BizAttributeCardRepresentation::new).collect(Collectors.toList());
        this.totalItemCount = select.getTotalItemCount();
    }

    @Data
    public static class BizAttributeCardRepresentation {
        private Long id;
        private String name;

        public BizAttributeCardRepresentation(BizAttribute attribute) {
            this.id = attribute.getId();
        }
    }
}

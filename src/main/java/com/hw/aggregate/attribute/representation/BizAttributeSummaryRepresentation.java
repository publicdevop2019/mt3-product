package com.hw.aggregate.attribute.representation;

import com.hw.aggregate.attribute.model.BizAttribute;
import com.hw.aggregate.attribute.model.MethodEnum;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class BizAttributeSummaryRepresentation {
    private List<BizAttributeCardRepresentation> data;

    public BizAttributeSummaryRepresentation(List<BizAttribute> content) {
        data = content.stream().map(BizAttributeCardRepresentation::new).collect(Collectors.toList());
    }

    @Data
    public static class BizAttributeCardRepresentation {
        private Long id;
        private String name;
        private Set<String> selectValues;
        private MethodEnum method;

        public BizAttributeCardRepresentation(BizAttribute attribute) {
            this.id = attribute.getId();
            this.name = attribute.getName();
            this.selectValues = attribute.getSelectValues();
            this.method = attribute.getMethod();
        }
    }
}

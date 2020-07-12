package com.hw.aggregate.attribute.representation;

import com.hw.aggregate.attribute.model.BizAttribute;
import com.hw.aggregate.attribute.model.AttributeMethod;
import com.hw.aggregate.attribute.model.BizAttributeType;
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
        private String description;
        private Set<String> selectValues;
        private AttributeMethod method;
        private BizAttributeType type;

        public BizAttributeCardRepresentation(BizAttribute attribute) {
            this.id = attribute.getId();
            this.name = attribute.getName();
            this.description = attribute.getDescription();
            this.selectValues = attribute.getSelectValues();
            this.method = attribute.getMethod();
            this.type = attribute.getType();
        }
    }
}

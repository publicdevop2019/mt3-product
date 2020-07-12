package com.hw.aggregate.attribute;

import com.hw.aggregate.attribute.command.CreateBizAttributeCommand;
import com.hw.aggregate.attribute.command.UpdateBizAttributeCommand;
import com.hw.aggregate.attribute.model.BizAttribute;
import com.hw.aggregate.attribute.representation.BizAttributeCreatedRepresentation;
import com.hw.aggregate.attribute.representation.BizAttributeSummaryRepresentation;
import com.hw.shared.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BizAttributeApplicationService {
    @Autowired
    BizAttributeRepository attributeRepository;
    @Autowired
    IdGenerator idGenerator;

    public BizAttributeSummaryRepresentation getAllAttributes() {
        List<BizAttribute> all = attributeRepository.findAll();
        return new BizAttributeSummaryRepresentation(all);
    }

    public BizAttributeCreatedRepresentation create(CreateBizAttributeCommand command) {
        BizAttribute attribute = BizAttribute.create(idGenerator.getId(), command, attributeRepository);
        return new BizAttributeCreatedRepresentation(attribute);
    }

    public void update(Long attributeId, UpdateBizAttributeCommand command) {
        BizAttribute.update(attributeId, command, attributeRepository);
    }

    public void delete(Long attributeId) {
        BizAttribute.delete(attributeId, attributeRepository);
    }
}

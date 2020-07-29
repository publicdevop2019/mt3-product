package com.hw.aggregate.attribute;

import com.hw.aggregate.attribute.command.CreateBizAttributeCommand;
import com.hw.aggregate.attribute.command.UpdateBizAttributeCommand;
import com.hw.aggregate.attribute.model.AdminQueryConfig;
import com.hw.aggregate.attribute.model.BizAttribute;
import com.hw.aggregate.attribute.representation.BizAttributeCreatedRepresentation;
import com.hw.aggregate.attribute.representation.BizAttributeSummaryRepresentation;
import com.hw.shared.IdGenerator;
import com.hw.shared.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BizAttributeApplicationService {
    @Autowired
    private BizAttributeRepository repo;
    @Autowired
    private IdGenerator idGenerator;
    @Autowired
    private AdminQueryConfig adminQueryConfig;

    @Transactional
    public BizAttributeCreatedRepresentation create(CreateBizAttributeCommand command) {
        BizAttribute attribute = BizAttribute.create(idGenerator.getId(), command, repo);
        return new BizAttributeCreatedRepresentation(attribute);
    }

    @Transactional
    public void update(Long id, UpdateBizAttributeCommand command) {
        BizAttribute read = BizAttribute.read(id, repo);
        read.update(command, repo);
    }

    @Transactional
    public void delete(Long attributeId) {
        BizAttribute.delete(attributeId, repo);
    }

    @Transactional(readOnly = true)
    public BizAttributeSummaryRepresentation getAllAttributes(Integer pageNumber, Integer pageSize, AdminQueryConfig.SortBy sortBy, SortOrder sortOrder) {
        PageRequest pageRequestAdmin = adminQueryConfig.getPageRequest(pageNumber, pageSize, sortBy, sortOrder);
        Page<BizAttribute> all = repo.findAll(pageRequestAdmin);
        return new BizAttributeSummaryRepresentation(all.getContent(), all.getTotalElements());
    }
}

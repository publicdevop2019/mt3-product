package com.hw.aggregate.attribute;

import com.hw.aggregate.attribute.command.CreateBizAttributeCommand;
import com.hw.aggregate.attribute.command.UpdateBizAttributeCommand;
import com.hw.aggregate.attribute.model.AdminQueryBuilder;
import com.hw.aggregate.attribute.model.BizAttribute;
import com.hw.aggregate.attribute.representation.BizAttributeAdminRepresentation;
import com.hw.aggregate.attribute.representation.BizAttributeCreatedRepresentation;
import com.hw.aggregate.attribute.representation.BizAttributeSummaryRepresentation;
import com.hw.shared.DefaultApplicationService;
import com.hw.shared.DefaultSumPagedRep;
import com.hw.shared.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@Service
public class BizAttributeApplicationService extends DefaultApplicationService {
    @Autowired
    private BizAttributeRepository repo;
    @Autowired
    private IdGenerator idGenerator;
    @Autowired
    private AdminQueryBuilder adminQueryBuilder;
    @Autowired
    private EntityManager entityManager;

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
    public BizAttributeSummaryRepresentation adminQuery(String search, String page, String countFlag) {
        DefaultSumPagedRep<BizAttribute> select = select(adminQueryBuilder, search, page, countFlag, BizAttribute.class);
        return new BizAttributeSummaryRepresentation(select);
    }

    @Transactional(readOnly = true)
    public BizAttributeAdminRepresentation getById(Long id) {
        return new BizAttributeAdminRepresentation(BizAttribute.read(id, repo));
    }
}

package com.hw.aggregate.attribute;

import com.hw.aggregate.attribute.command.CreateBizAttributeCommand;
import com.hw.aggregate.attribute.command.UpdateBizAttributeCommand;
import com.hw.aggregate.attribute.model.AdminAttributeSelectQueryBuilder;
import com.hw.aggregate.attribute.model.BizAttribute;
import com.hw.aggregate.attribute.representation.BizAttributeAdminRep;
import com.hw.aggregate.attribute.representation.BizAttributeCreatedRep;
import com.hw.aggregate.attribute.representation.BizAttributeSumRep;
import com.hw.shared.DefaultApplicationService;
import com.hw.shared.SumPagedRep;
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
    private AdminAttributeSelectQueryBuilder adminQueryBuilder;

    @Transactional
    public BizAttributeCreatedRep create(CreateBizAttributeCommand command) {
        BizAttribute attribute = BizAttribute.create(idGenerator.getId(), command, repo);
        return new BizAttributeCreatedRep(attribute);
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
    public BizAttributeSumRep adminQuery(String search, String page, String countFlag) {
        SumPagedRep<BizAttribute> select = select(adminQueryBuilder, search, page, countFlag, BizAttribute.class);
        return new BizAttributeSumRep(select);
    }

    @Transactional(readOnly = true)
    public BizAttributeAdminRep getById(Long id) {
        return new BizAttributeAdminRep(BizAttribute.read(id, repo));
    }
}

package com.hw.aggregate.attribute;

import com.hw.aggregate.attribute.command.CreateBizAttributeCommand;
import com.hw.aggregate.attribute.command.UpdateBizAttributeCommand;
import com.hw.aggregate.attribute.exception.BizAttributeNotFoundException;
import com.hw.aggregate.attribute.model.BizAttribute;
import com.hw.aggregate.attribute.model.BizAttributeManager;
import com.hw.aggregate.attribute.representation.BizAttributeAdminRep;
import com.hw.aggregate.attribute.representation.BizAttributeCreatedRep;
import com.hw.aggregate.attribute.representation.BizAttributeAdminSumRep;
import com.hw.shared.IdGenerator;
import com.hw.shared.sql.RestfulEntityManager;
import com.hw.shared.sql.SumPagedRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BizAttributeApplicationService {
    @Autowired
    private BizAttributeRepository repo;
    @Autowired
    private IdGenerator idGenerator;
    @Autowired
    private BizAttributeManager bizAttributeManager;

    @Transactional
    public BizAttributeCreatedRep createForAdmin(CreateBizAttributeCommand command) {
        BizAttribute attribute = BizAttribute.create(idGenerator.getId(), command, repo);
        return new BizAttributeCreatedRep(attribute);
    }

    @Transactional
    public void replaceForAdminById(Long id, UpdateBizAttributeCommand command) {
        SumPagedRep<BizAttribute> bizAttributeSumPagedRep = bizAttributeManager.readById(RestfulEntityManager.RoleEnum.ADMIN, id.toString(), BizAttribute.class);
        if (bizAttributeSumPagedRep.getData().size() == 0)
            throw new BizAttributeNotFoundException();
        bizAttributeSumPagedRep.getData().get(0).update(command, repo);
    }

    @Transactional
    public void deleteForAdminById(Long attributeId) {
        bizAttributeManager.deleteById(RestfulEntityManager.RoleEnum.ADMIN, attributeId.toString(), BizAttribute.class);
    }

    @Transactional(readOnly = true)
    public BizAttributeAdminSumRep readForAdminByQuery(String search, String page, String countFlag) {
        SumPagedRep<BizAttribute> bizAttributeSumPagedRep = bizAttributeManager.readByQuery(RestfulEntityManager.RoleEnum.ADMIN, search, page, countFlag, BizAttribute.class);
        return new BizAttributeAdminSumRep(bizAttributeSumPagedRep);
    }

    @Transactional(readOnly = true)
    public BizAttributeAdminRep readForAdminById(Long id) {
        SumPagedRep<BizAttribute> bizAttributeSumPagedRep = bizAttributeManager.readById(RestfulEntityManager.RoleEnum.ADMIN, id.toString(), BizAttribute.class);
        if (bizAttributeSumPagedRep.getData().size() == 0)
            throw new BizAttributeNotFoundException();
        return new BizAttributeAdminRep(bizAttributeSumPagedRep.getData().get(0));
    }

    @Transactional(readOnly = true)
    public BizAttributeAdminSumRep readForAppByQuery(String search, String page, String countFlag) {
        SumPagedRep<BizAttribute> bizAttributeSumPagedRep = bizAttributeManager.readByQuery(RestfulEntityManager.RoleEnum.APP, search, page, countFlag, BizAttribute.class);
        return new BizAttributeAdminSumRep(bizAttributeSumPagedRep);
    }
}

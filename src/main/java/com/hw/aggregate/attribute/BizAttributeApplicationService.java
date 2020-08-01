package com.hw.aggregate.attribute;

import com.hw.aggregate.attribute.command.CreateBizAttributeCommand;
import com.hw.aggregate.attribute.command.UpdateBizAttributeCommand;
import com.hw.aggregate.attribute.model.AdminQueryBuilder;
import com.hw.aggregate.attribute.model.BizAttribute;
import com.hw.aggregate.attribute.representation.BizAttributeAdminRepresentation;
import com.hw.aggregate.attribute.representation.BizAttributeCreatedRepresentation;
import com.hw.aggregate.attribute.representation.BizAttributeSummaryRepresentation;
import com.hw.shared.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

@Service
public class BizAttributeApplicationService {
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
    public BizAttributeSummaryRepresentation adminQuery(String query, String page, String countFlag) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<BizAttribute> query0 = cb.createQuery(BizAttribute.class);
        Root<BizAttribute> root = query0.from(BizAttribute.class);

        PageRequest pageRequest = adminQueryBuilder.getPageRequest(page);
        Predicate queryClause = adminQueryBuilder.getQueryClause(cb, root, query);
        List<BizAttribute> query1 = repo.query(entityManager, cb, query0, root, queryClause, pageRequest);
        Long aLong = null;
        if (!"0".equals(countFlag)) {
            aLong = repo.queryCount(entityManager, cb, queryClause);
        }
        return new BizAttributeSummaryRepresentation(query1, aLong);
    }

    @Transactional(readOnly = true)
    public BizAttributeAdminRepresentation getById(Long id) {
        return new BizAttributeAdminRepresentation(BizAttribute.read(id, repo));
    }
}

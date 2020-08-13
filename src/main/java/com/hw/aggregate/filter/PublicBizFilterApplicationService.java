package com.hw.aggregate.filter;

import com.hw.aggregate.filter.model.BizFilter;
import com.hw.aggregate.filter.model.BizFilterQueryRegistry;
import com.hw.aggregate.filter.representation.PublicBizFilterCardRep;
import com.hw.shared.IdGenerator;
import com.hw.shared.rest.CreatedRep;
import com.hw.shared.rest.DefaultRoleBasedRestfulService;
import com.hw.shared.rest.VoidTypedClass;
import com.hw.shared.sql.RestfulEntityManager;
import com.hw.shared.sql.SumPagedRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PublicBizFilterApplicationService extends DefaultRoleBasedRestfulService<BizFilter, PublicBizFilterCardRep, Void, VoidTypedClass> {
    @Autowired
    private BizFilterRepository repo2;

    @Autowired
    private IdGenerator idGenerator2;

    @Autowired
    private BizFilterQueryRegistry bizFilterManager2;

    @PostConstruct
    private void setUp() {
        repo = repo2;
        idGenerator = idGenerator2;
        restfulEntityManager = bizFilterManager2;
        entityClass = BizFilter.class;
        role = RestfulEntityManager.RoleEnum.PUBLIC;
    }

    @Transactional(readOnly = true)
    @Override
    public SumPagedRep<PublicBizFilterCardRep> readByQuery(String query, String page, String config) {
        SumPagedRep<BizFilter> select1 = restfulEntityManager.readByQuery(role, query, page, config, entityClass);
        List<BizFilter> data = select1.getData();
        List<PublicBizFilterCardRep> collect = null;
        if (data.size() != 0) {
            collect = select1.getData().get(0).getFilterItems().stream().map(PublicBizFilterCardRep::new).collect(Collectors.toList());
        }
        return new SumPagedRep<>(collect, null);
    }

    @Override
    public BizFilter replaceEntity(BizFilter bizFilter, Object command) {
        return null;
    }

    @Override
    public PublicBizFilterCardRep getEntitySumRepresentation(BizFilter bizFilter) {
        return null;
    }


    @Override
    public Void getEntityRepresentation(BizFilter bizFilter) {
        return null;
    }

    @Override
    protected BizFilter createEntity(long id, Object command) {
        return null;
    }
}

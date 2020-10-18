package com.hw.aggregate.filter;

import com.hw.aggregate.filter.model.BizFilter;
import com.hw.aggregate.filter.representation.PublicBizFilterCardRep;
import com.hw.shared.rest.DefaultRoleBasedRestfulService;
import com.hw.shared.rest.VoidTypedClass;
import com.hw.shared.sql.RestfulQueryRegistry;
import com.hw.shared.sql.SumPagedRep;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PublicBizFilterApplicationService extends DefaultRoleBasedRestfulService<BizFilter, PublicBizFilterCardRep, Void, VoidTypedClass> {

    @PostConstruct
    private void setUp() {
        entityClass = BizFilter.class;
        role = RestfulQueryRegistry.RoleEnum.PUBLIC;
    }

    @Transactional(readOnly = true)
    @Override
    public SumPagedRep<PublicBizFilterCardRep> readByQuery(String query, String page, String config) {
        SumPagedRep<BizFilter> select1 = queryRegistry.readByQuery(role, query, page, config, entityClass);
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

    @Override
    public void preDelete(BizFilter bizFilter) {

    }

    @Override
    public void postDelete(BizFilter bizFilter) {

    }

    @Override
    protected void prePatch(BizFilter bizFilter, Map<String, Object> params, VoidTypedClass middleLayer) {

    }

    @Override
    protected void postPatch(BizFilter bizFilter, Map<String, Object> params, VoidTypedClass middleLayer) {

    }
}

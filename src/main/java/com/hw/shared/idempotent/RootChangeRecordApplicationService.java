package com.hw.shared.idempotent;

import com.hw.shared.idempotent.model.ChangeRecord;
import com.hw.shared.idempotent.model.ChangeRecordQueryRegistry;
import com.hw.shared.idempotent.representation.RootChangeRecordCardRep;
import com.hw.shared.rest.DefaultRoleBasedRestfulService;
import com.hw.shared.rest.VoidTypedClass;
import com.hw.shared.rest.exception.EntityNotExistException;
import com.hw.shared.sql.RestfulQueryRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service
public class RootChangeRecordApplicationService extends DefaultRoleBasedRestfulService<ChangeRecord, RootChangeRecordCardRep, Void, VoidTypedClass> {
    @Autowired
    private ApplicationContext context;
    @Autowired
    private ChangeRepository changeRepository;
    @Autowired
    private ChangeRecordQueryRegistry changeRecordQueryRegistry;

    @PostConstruct
    private void setUp() {
        repo = changeRepository;
        queryRegistry = changeRecordQueryRegistry;
        entityClass = ChangeRecord.class;
        role = RestfulQueryRegistry.RoleEnum.ROOT;
    }

    @Transactional
    public void deleteById(Long id) {
        ChangeRecord changeRecord = changeRepository.findById(id).orElseThrow(EntityNotExistException::new);
        Class<?> aClass = null;
        try {
            aClass = Class.forName(changeRecord.getServiceBeanName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        DefaultRoleBasedRestfulService bean = (DefaultRoleBasedRestfulService) context.getBean(aClass);
        bean.rollback(changeRecord.getChangeId());
    }

    @Override
    public ChangeRecord replaceEntity(ChangeRecord changeRecord, Object command) {
        return null;
    }

    @Override
    public RootChangeRecordCardRep getEntitySumRepresentation(ChangeRecord changeRecord) {
        return new RootChangeRecordCardRep(changeRecord);
    }

    @Override
    public Void getEntityRepresentation(ChangeRecord changeRecord) {
        return null;
    }

    @Override
    protected ChangeRecord createEntity(long id, Object command) {
        return null;
    }

    @Override
    public void preDelete(ChangeRecord changeRecord) {
    }

    @Override
    public void postDelete(ChangeRecord changeRecord) {

    }

    @Override
    protected void prePatch(ChangeRecord changeRecord, Map<String, Object> params, VoidTypedClass middleLayer) {

    }

    @Override
    protected void postPatch(ChangeRecord changeRecord, Map<String, Object> params, VoidTypedClass middleLayer) {

    }
}
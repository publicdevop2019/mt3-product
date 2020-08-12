package com.hw.aggregate.attribute;

import com.hw.aggregate.attribute.command.CreateBizAttributeCommand;
import com.hw.aggregate.attribute.command.UpdateBizAttributeCommand;
import com.hw.shared.IdGenerator;
import com.hw.shared.sql.RestfulEntityManager;
import com.hw.shared.sql.SumPagedRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

public abstract class DefaultRoleBasedApplicationService<T> {

    @Autowired
    private JpaRepository<T, Long> repo;
    @Autowired
    private IdGenerator idGenerator;
    @Autowired
    private RestfulEntityManager<T> restfulEntityManager;

    private Class<T> entityClass;

    private RestfulEntityManager.RoleEnum role;

    @Transactional
    public <S> S create(CreateBizAttributeCommand command) {
        T created = createEntity(idGenerator.getId(), command);
        repo.save(created);
        return getCreatedEntityRepresentation(created);
    }

    @Transactional
    public void replaceById(Long id, UpdateBizAttributeCommand command) {
        SumPagedRep<T> tSumPagedRep = getEntityById(id);
        T after = replaceEntity(tSumPagedRep.getData().get(0), command);
        repo.save(after);
    }


    @Transactional
    public void deleteById(Long attributeId) {
        restfulEntityManager.deleteById(role, attributeId.toString(), entityClass);
    }

    @Transactional(readOnly = true)
    public <S> SumPagedRep<S> readByQuery(String query, String page, String config) {
        SumPagedRep<T> tSumPagedRep = restfulEntityManager.readByQuery(role, query, page, config, entityClass);
        List<S> col = tSumPagedRep.getData().stream().map(this::<S>getEntitySumRepresentation).collect(Collectors.toList());
        return new SumPagedRep<>(col, tSumPagedRep.getTotalItemCount());
    }


    @Transactional(readOnly = true)
    public <S> S readById(Long id) {
        SumPagedRep<T> tSumPagedRep = getEntityById(id);
        return getEntityRepresentation(tSumPagedRep.getData().get(0));
    }

    private SumPagedRep<T> getEntityById(Long id) {
        SumPagedRep<T> tSumPagedRep = restfulEntityManager.readById(role, id.toString(), entityClass);
        if (tSumPagedRep.getData().size() == 0)
            throw new EntityNotFoundException();
        return tSumPagedRep;
    }

    public abstract T replaceEntity(T t, Object command);

    public abstract <S> S getEntitySumRepresentation(T t);

    public abstract <S> S getEntityRepresentation(T t);

    protected abstract <S> S getCreatedEntityRepresentation(T created);

    protected abstract T createEntity(long id, Object command);
}

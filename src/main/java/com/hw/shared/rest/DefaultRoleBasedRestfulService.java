package com.hw.shared.rest;

import com.hw.shared.IdGenerator;
import com.hw.shared.sql.RestfulEntityManager;
import com.hw.shared.sql.SumPagedRep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

public abstract class DefaultRoleBasedRestfulService<T, X, Y> {

    protected JpaRepository<T, Long> repo;
    protected IdGenerator idGenerator;
    protected RestfulEntityManager<T> restfulEntityManager;

    protected Class<T> entityClass;

    protected RestfulEntityManager.RoleEnum role;

    @Transactional
    public <S extends CreatedRep> S create(Object command) {
        T created = createEntity(idGenerator.getId(), command);
        repo.save(created);
        return getCreatedEntityRepresentation(created);
    }

    @Transactional
    public void replaceById(Long id, Object command) {
        SumPagedRep<T> tSumPagedRep = getEntityById(id);
        T after = replaceEntity(tSumPagedRep.getData().get(0), command);
        repo.save(after);
    }


    @Transactional
    public void deleteById(Long id) {
        restfulEntityManager.deleteById(role, id.toString(), entityClass);
    }

    @Transactional(readOnly = true)
    public SumPagedRep<X> readByQuery(String query, String page, String config) {
        SumPagedRep<T> tSumPagedRep = restfulEntityManager.readByQuery(role, query, page, config, entityClass);
        List<X> col = tSumPagedRep.getData().stream().map(this::getEntitySumRepresentation).collect(Collectors.toList());
        return new SumPagedRep<>(col, tSumPagedRep.getTotalItemCount());
    }


    @Transactional(readOnly = true)
    public Y readById(Long id) {
        SumPagedRep<T> tSumPagedRep = getEntityById(id);
        return getEntityRepresentation(tSumPagedRep.getData().get(0));
    }

    private SumPagedRep<T> getEntityById(Long id) {
        SumPagedRep<T> tSumPagedRep = restfulEntityManager.readById(role, id.toString(), entityClass);
        if (tSumPagedRep.getData().size() == 0)
            throw new EntityNotExistException();
        return tSumPagedRep;
    }

    public abstract T replaceEntity(T t, Object command);

    public abstract X getEntitySumRepresentation(T t);

    public abstract Y getEntityRepresentation(T t);

    protected abstract <S extends CreatedRep> S getCreatedEntityRepresentation(T created);

    protected abstract T createEntity(long id, Object command);
}

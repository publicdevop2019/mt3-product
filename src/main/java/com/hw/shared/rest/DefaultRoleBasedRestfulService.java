package com.hw.shared.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.hw.aggregate.product.exception.ProductDetailPatchException;
import com.hw.shared.IdGenerator;
import com.hw.shared.sql.RestfulEntityManager;
import com.hw.shared.sql.SumPagedRep;
import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class DefaultRoleBasedRestfulService<T, X, Y, Z> {

    protected JpaRepository<T, Long> repo;
    protected IdGenerator idGenerator;
    protected RestfulEntityManager<T> restfulEntityManager;

    protected Class<T> entityClass;

    protected Class<Z> entityPatchClass;

    protected Function<T, Z> entityPatchSupplier;

    protected RestfulEntityManager.RoleEnum role;
    protected ObjectMapper om;

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
    public void patchById(Long id, JsonPatch patch) {
        SumPagedRep<T> entityById = getEntityById(id);
        T original = entityById.getData().get(0);
        Z command = entityPatchSupplier.apply(original);
        Z patchMiddleLayer;
        try {
            JsonNode jsonNode = om.convertValue(command, JsonNode.class);
            JsonNode patchedNode = patch.apply(jsonNode);
            patchMiddleLayer = om.treeToValue(patchedNode, entityPatchClass);
        } catch (JsonPatchException | JsonProcessingException e) {
            e.printStackTrace();
            throw new ProductDetailPatchException();
        }
        BeanUtils.copyProperties(patchMiddleLayer, original);
        repo.save(original);
    }

    @Transactional
    public Integer deleteById(Long id) {
        return restfulEntityManager.deleteById(role, id.toString(), entityClass);
    }

    @Transactional
    public Integer deleteByQuery(String query) {
        return restfulEntityManager.deleteByQuery(role, query, entityClass);
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

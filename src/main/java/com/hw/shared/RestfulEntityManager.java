package com.hw.shared;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class RestfulEntityManager<T> {
    public enum RoleEnum {
        ROOT,
        ADMIN,
        CUSTOMER,
        APP,
        PUBLIC
    }

    protected Map<RoleEnum, SelectQueryBuilder<T>> selectQueryBuilder = new HashMap<>();
    protected Map<RoleEnum, UpdateQueryBuilder<T>> updateQueryBuilder = new HashMap<>();
    protected Map<RoleEnum, DeleteQueryBuilder<T>> deleteQueryBuilder = new HashMap<>();

    protected abstract void configQueryBuilder();

    //    abstract <S> T create(S command);


    //GET service-name/role-name/entity-collection - read object collection with pagination
    //GET service-name/role-name/object-collection?query={condition-clause}
    public SumPagedRep<T> readByQuery(RoleEnum roleEnum, String query, String page, String config, Class<T> clazz) {
        SelectQueryBuilder<T> selectQueryBuilder = this.selectQueryBuilder.get(roleEnum);
        if (selectQueryBuilder == null)
            throw new QueryBuilderNotFoundException();
        List<T> select = selectQueryBuilder.select(query, page, clazz);
        Long aLong = null;
        if (!skipCount(config)) {
            aLong = selectQueryBuilder.selectCount(query, clazz);
        }
        return new SumPagedRep<>(select, aLong);
    }

    // convert GET service-name/role-name/entity-collection/{entity-id} to ByQuery
    public SumPagedRep<T> readById(RoleEnum roleEnum, String id, Class<T> clazz) {
        return readByQuery(roleEnum, convertIdToQuery(id), null, skipCount(), clazz);
    }

    public Integer deleteByQuery(RoleEnum roleEnum, String query, Class<T> clazz) {
        DeleteQueryBuilder<T> deleteQueryBuilder = this.deleteQueryBuilder.get(roleEnum);
        if (selectQueryBuilder == null)
            throw new QueryBuilderNotFoundException();
        return deleteQueryBuilder.delete(query, clazz);
    }

    public Integer deleteById(RoleEnum roleEnum, String id, Class<T> clazz) {
        return deleteByQuery(roleEnum, convertIdToQuery(id), clazz);
    }

    public Integer update(RoleEnum roleEnum, List<PatchCommand> commands, Class<T> clazz) {
        UpdateQueryBuilder<T> updateQueryBuilder = this.updateQueryBuilder.get(roleEnum);
        if (selectQueryBuilder == null)
            throw new QueryBuilderNotFoundException();
        return updateQueryBuilder.update(commands, clazz);
    }

    //config=sc:0
    private boolean skipCount(String config) {
        return config != null && config.contains("sc:1");
    }

    private String skipCount() {
        return "sc:1";
    }

    private String convertIdToQuery(String id) {
        return "id:" + id;
    }

}
package com.mt.mall.application.catalog;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.mall.application.ApplicationServiceRegistry;
import com.mt.mall.application.catalog.command.CreateCatalogCommand;
import com.mt.mall.application.catalog.command.PatchCatalogCommand;
import com.mt.mall.application.catalog.command.UpdateCatalogCommand;
import com.mt.mall.domain.DomainRegistry;
import com.mt.mall.domain.model.catalog.Catalog;
import com.mt.mall.domain.model.catalog.CatalogId;
import com.mt.mall.domain.model.catalog.CatalogQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CatalogApplicationService {
    @SubscribeForEvent
    @Transactional
    public String create(CreateCatalogCommand command, String operationId) {
        CatalogId catalogId = new CatalogId();
        return ApplicationServiceRegistry.idempotentWrapper().idempotentCreate(command, operationId, catalogId,
                () -> DomainRegistry.catalogService().create(
                        catalogId,
                        command.getName(),
                        new CatalogId(command.getParentId()),
                        command.getAttributes(),
                        command.getCatalogType()
                ), Catalog.class
        );
    }

    public SumPagedRep<Catalog> catalogs(String queryParam, String pageParam, String skipCount) {
        return DomainRegistry.catalogRepository().catalogsOfQuery(new CatalogQuery(queryParam, pageParam, skipCount));
    }

    public SumPagedRep<Catalog> publicCatalogs(String pageParam, String skipCount) {
        return DomainRegistry.catalogRepository().catalogsOfQuery(CatalogQuery.publicQuery(pageParam, skipCount));
    }

    public Optional<Catalog> catalog(String id) {
        return DomainRegistry.catalogRepository().catalogOfId(new CatalogId(id));
    }

    @SubscribeForEvent
    @Transactional
    public void replace(String id, UpdateCatalogCommand command, String changeId) {
        CatalogId catalogId = new CatalogId(id);
        ApplicationServiceRegistry.idempotentWrapper().idempotent(catalogId, command, changeId, (change) -> {
            Optional<Catalog> optionalCatalog = DomainRegistry.catalogRepository().catalogOfId(catalogId);
            if (optionalCatalog.isPresent()) {
                Catalog catalog = optionalCatalog.get();
                catalog.replace(command.getName(), new CatalogId(command.getParentId()), command.getAttributes(), command.getCatalogType());
                DomainRegistry.catalogRepository().add(catalog);
            }
        }, Catalog.class);
    }

    @SubscribeForEvent
    @Transactional
    public void removeCatalog(String id, String changeId) {
        CatalogId catalogId = new CatalogId(id);
        ApplicationServiceRegistry.idempotentWrapper().idempotent(catalogId, null, changeId, (change) -> {
            Optional<Catalog> optionalCatalog = DomainRegistry.catalogRepository().catalogOfId(catalogId);
            if (optionalCatalog.isPresent()) {
                Catalog catalog = optionalCatalog.get();
                DomainRegistry.catalogRepository().remove(catalog);
            }
        }, Catalog.class);
    }

    @SubscribeForEvent
    @Transactional
    public Set<String> removeCatalogs(String queryParam, String changeId) {
        return ApplicationServiceRegistry.idempotentWrapper().idempotentDeleteByQuery(queryParam, changeId, (change) -> {
            Set<Catalog> allClientsOfQuery = QueryUtility.getAllByQuery((query) -> DomainRegistry.catalogRepository().catalogsOfQuery((CatalogQuery) query), new CatalogQuery(queryParam));
            DomainRegistry.catalogRepository().remove(allClientsOfQuery);
            change.setRequestBody(allClientsOfQuery);
            change.setDeletedIds(allClientsOfQuery.stream().map(e -> e.getCatalogId().getDomainId()).collect(Collectors.toSet()));
            change.setQuery(queryParam);
            return allClientsOfQuery.stream().map(Catalog::getCatalogId).collect(Collectors.toSet());
        }, Catalog.class);
    }

    @SubscribeForEvent
    @Transactional
    public void patch(String id, JsonPatch command, String changeId) {
        CatalogId catalogId = new CatalogId(id);
        ApplicationServiceRegistry.idempotentWrapper().idempotent(catalogId, command, changeId, (ignored) -> {
            Optional<Catalog> optionalCatalog = DomainRegistry.catalogRepository().catalogOfId(catalogId);
            if (optionalCatalog.isPresent()) {
                Catalog catalog = optionalCatalog.get();
                PatchCatalogCommand beforePatch = new PatchCatalogCommand(catalog);
                PatchCatalogCommand afterPatch = CommonDomainRegistry.getCustomObjectSerializer().applyJsonPatch(command, beforePatch, PatchCatalogCommand.class);
                catalog.replace(
                        afterPatch.getName(),
                        new CatalogId(afterPatch.getParentId()),
                        afterPatch.getAttributes(),
                        afterPatch.getType()
                );
            }
        }, Catalog.class);
    }
}

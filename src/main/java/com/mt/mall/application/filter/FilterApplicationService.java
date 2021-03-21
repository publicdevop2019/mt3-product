package com.mt.mall.application.filter;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.mall.application.ApplicationServiceRegistry;
import com.mt.mall.application.filter.command.CreateFilterCommand;
import com.mt.mall.application.filter.command.PatchFilterCommand;
import com.mt.mall.application.filter.command.UpdateFilterCommand;
import com.mt.mall.application.filter.representation.FilterCardRepresentation;
import com.mt.mall.domain.DomainRegistry;
import com.mt.mall.domain.model.catalog.CatalogId;
import com.mt.mall.domain.model.filter.Filter;
import com.mt.mall.domain.model.filter.FilterId;
import com.mt.mall.domain.model.filter.FilterItem;
import com.mt.mall.domain.model.filter.FilterQuery;
import com.mt.mall.domain.model.meta.Meta;
import com.mt.mall.domain.model.meta.MetaQuery;
import com.mt.mall.domain.model.tag.TagId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilterApplicationService {

    @SubscribeForEvent
    @Transactional
    public String create(CreateFilterCommand command, String operationId) {
        FilterId filterId = new FilterId();
        return ApplicationServiceRegistry.getIdempotentWrapper().idempotentCreate(command, operationId, filterId,
                () -> DomainRegistry.getFilterService().create(
                        filterId,
                        command.getDescription(),
                        command.getCatalogs().stream().map(CatalogId::new).collect(Collectors.toSet()),
                        command.getFilters().stream().map(e -> new FilterItem(new TagId(e.getId()), e.getName(), e.getValues())).collect(Collectors.toSet())
                ), Filter.class
        );
    }

    public SumPagedRep<FilterCardRepresentation> filters(String queryParam, String pageParam, String skipCount) {
        SumPagedRep<Filter> filterSumPagedRep = DomainRegistry.getFilterRepository().filtersOfQuery(new FilterQuery(queryParam, pageParam, skipCount, false));
        Set<FilterId> collect = filterSumPagedRep.getData().stream().map(Filter::getFilterId).collect(Collectors.toSet());
        Set<Meta> allByQuery = QueryUtility.getAllByQuery(e -> DomainRegistry.getMetaRepository().metaOfQuery((MetaQuery) e), new MetaQuery(new HashSet<>(collect)));
        List<FilterCardRepresentation> collect1 = filterSumPagedRep.getData().stream().map(e -> {
            boolean review = false;
            Optional<Meta> first = allByQuery.stream().filter(ee -> ee.getDomainId().getDomainId().equalsIgnoreCase(e.getFilterId().getDomainId())).findFirst();
            if (first.isPresent() && first.get().getHasChangedTag()) {
                review = true;
            }
            return new FilterCardRepresentation(e, review);
        }).collect(Collectors.toList());
        return new SumPagedRep<>(collect1, filterSumPagedRep.getTotalItemCount());
    }

    public SumPagedRep<Filter> publicFilters(String queryParam, String pageParam, String skipCount) {
        return DomainRegistry.getFilterRepository().filtersOfQuery(new FilterQuery(queryParam, pageParam, skipCount, true));
    }

    public Optional<Filter> filter(String id) {
        return DomainRegistry.getFilterRepository().filterOfId(new FilterId(id));
    }

    @SubscribeForEvent
    @Transactional
    public void replace(String id, UpdateFilterCommand command, String changeId) {
        FilterId filterId = new FilterId(id);
        ApplicationServiceRegistry.getIdempotentWrapper().idempotent(filterId, command, changeId, (ignored) -> {
            Optional<Filter> optionalFilter = DomainRegistry.getFilterRepository().filterOfId(filterId);
            if (optionalFilter.isPresent()) {
                Filter filter = optionalFilter.get();
                filter.replace(
                        command.getCatalogs().stream().map(CatalogId::new).collect(Collectors.toSet()),
                        command.getFilters().stream().map(e -> new FilterItem(new TagId(e.getId()), e.getName(), e.getValues())).collect(Collectors.toSet()),
                        command.getDescription());
                DomainRegistry.getFilterRepository().add(filter);
            }
        }, Filter.class);
    }

    @SubscribeForEvent
    @Transactional
    public void removeFilter(String id, String changeId) {
        FilterId filterId = new FilterId(id);
        ApplicationServiceRegistry.getIdempotentWrapper().idempotent(filterId, null, changeId, (change) -> {
            Optional<Filter> optionalFilter = DomainRegistry.getFilterRepository().filterOfId(filterId);
            if (optionalFilter.isPresent()) {
                Filter filter = optionalFilter.get();
                DomainRegistry.getFilterRepository().remove(filter);
            }
        }, Filter.class);
    }

    @SubscribeForEvent
    @Transactional
    public Set<String> removeFilters(String queryParam, String changeId) {
        return ApplicationServiceRegistry.getIdempotentWrapper().idempotentDeleteByQuery(queryParam, changeId, (change) -> {
            Set<Filter> filters = QueryUtility.getAllByQuery((query) -> DomainRegistry.getFilterRepository().filtersOfQuery((FilterQuery) query), new FilterQuery(queryParam));
            DomainRegistry.getFilterRepository().remove(filters);
            change.setRequestBody(filters);
            change.setDeletedIds(filters.stream().map(e -> e.getFilterId().getDomainId()).collect(Collectors.toSet()));
            change.setQuery(queryParam);
            return filters.stream().map(Filter::getFilterId).collect(Collectors.toSet());
        }, Filter.class);
    }

    @SubscribeForEvent
    @Transactional
    public void patch(String id, JsonPatch command, String changeId) {
        FilterId filterId = new FilterId(id);
        ApplicationServiceRegistry.getIdempotentWrapper().idempotent(filterId, command, changeId, (ignored) -> {
            Optional<Filter> optionalCatalog = DomainRegistry.getFilterRepository().filterOfId(filterId);
            if (optionalCatalog.isPresent()) {
                Filter filter = optionalCatalog.get();
                PatchFilterCommand beforePatch = new PatchFilterCommand(filter);
                PatchFilterCommand afterPatch = CommonDomainRegistry.getCustomObjectSerializer().applyJsonPatch(command, beforePatch, PatchFilterCommand.class);
                filter.replace(
                        afterPatch.getCatalogs().stream().map(CatalogId::new).collect(Collectors.toSet()),
                        afterPatch.getDescription()
                );
            }
        }, Filter.class);
    }

}

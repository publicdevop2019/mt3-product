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
import com.mt.mall.domain.DomainRegistry;
import com.mt.mall.domain.model.filter.Filter;
import com.mt.mall.domain.model.filter.FilterId;
import com.mt.mall.domain.model.filter.FilterItem;
import com.mt.mall.domain.model.filter.FilterQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilterApplicationService {

    @SubscribeForEvent
    @Transactional
    public String create(CreateFilterCommand command, String operationId) {
        FilterId filterId = new FilterId();
        return ApplicationServiceRegistry.idempotentWrapper().idempotentCreate(command, operationId, filterId,
                () -> DomainRegistry.filterService().create(
                        filterId,
                        command.getDescription(),
                        command.getCatalogs(),
                        command.getFilters().stream().map(e -> new FilterItem(e.getId(), e.getName(), e.getValues())).collect(Collectors.toSet())
                ), Filter.class
        );
    }

    public SumPagedRep<Filter> filters(String queryParam, String pageParam, String skipCount) {
        return DomainRegistry.filterRepository().filtersOfQuery(new FilterQuery(queryParam, pageParam, skipCount, false));
    }

    public SumPagedRep<Filter> publicFilters(String queryParam, String pageParam, String skipCount) {
        return DomainRegistry.filterRepository().filtersOfQuery(new FilterQuery(queryParam, pageParam, skipCount, true));
    }

    public Optional<Filter> filter(String id) {
        return DomainRegistry.filterRepository().filterOfId(new FilterId(id));
    }

    @SubscribeForEvent
    @Transactional
    public void replace(String id, UpdateFilterCommand command, String changeId) {
        FilterId filterId = new FilterId(id);
        ApplicationServiceRegistry.idempotentWrapper().idempotent(filterId, command, changeId, (ignored) -> {
            Optional<Filter> optionalFilter = DomainRegistry.filterRepository().filterOfId(filterId);
            if (optionalFilter.isPresent()) {
                Filter filter = optionalFilter.get();
                filter.replace(
                        command.getCatalogs(),
                        command.getFilters().stream().map(e -> new FilterItem(e.getId(), e.getName(), e.getValues())).collect(Collectors.toSet()),
                        command.getDescription());
                DomainRegistry.filterRepository().add(filter);
            }
        }, Filter.class);
    }

    @SubscribeForEvent
    @Transactional
    public void removeFilter(String id, String changeId) {
        FilterId filterId = new FilterId(id);
        ApplicationServiceRegistry.idempotentWrapper().idempotent(filterId, null, changeId, (change) -> {
            Optional<Filter> optionalFilter = DomainRegistry.filterRepository().filterOfId(filterId);
            if (optionalFilter.isPresent()) {
                Filter filter = optionalFilter.get();
                DomainRegistry.filterRepository().remove(filter);
            }
        }, Filter.class);
    }

    @SubscribeForEvent
    @Transactional
    public Set<String> removeFilters(String queryParam, String changeId) {
        return ApplicationServiceRegistry.idempotentWrapper().idempotentDeleteByQuery(queryParam, changeId, (change) -> {
            Set<Filter> filters = QueryUtility.getAllByQuery((query) -> DomainRegistry.filterRepository().filtersOfQuery((FilterQuery) query), new FilterQuery(queryParam));
            DomainRegistry.filterRepository().remove(filters);
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
        ApplicationServiceRegistry.idempotentWrapper().idempotent(filterId, command, changeId, (ignored) -> {
            Optional<Filter> optionalCatalog = DomainRegistry.filterRepository().filterOfId(filterId);
            if (optionalCatalog.isPresent()) {
                Filter filter = optionalCatalog.get();
                PatchFilterCommand beforePatch = new PatchFilterCommand(filter);
                PatchFilterCommand afterPatch = CommonDomainRegistry.getCustomObjectSerializer().applyJsonPatch(command, beforePatch, PatchFilterCommand.class);
                filter.replace(
                        afterPatch.getCatalogs(),
                        afterPatch.getDescription()
                );
            }
        }, Filter.class);
    }

}

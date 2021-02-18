package com.mt.mall.application.sku;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.common.domain.model.CommonDomainRegistry;
import com.mt.common.domain_event.SubscribeForEvent;
import com.mt.common.persistence.QueryConfig;
import com.mt.common.query.PageConfig;
import com.mt.common.query.QueryUtility;
import com.mt.common.sql.PatchCommand;
import com.mt.common.sql.SumPagedRep;
import com.mt.mall.application.ApplicationServiceRegistry;
import com.mt.mall.application.sku.command.CreateSkuCommand;
import com.mt.mall.application.sku.command.PatchSkuCommand;
import com.mt.mall.application.sku.command.UpdateSkuCommand;
import com.mt.mall.domain.DomainRegistry;
import com.mt.mall.domain.model.sku.Sku;
import com.mt.mall.domain.model.sku.SkuId;
import com.mt.mall.domain.model.sku.SkuQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SkuApplicationService {

    @SubscribeForEvent
    @Transactional
    public String create(CreateSkuCommand command, String operationId) {
        SkuId skuId = DomainRegistry.skuRepository().nextIdentity();
        return ApplicationServiceRegistry.idempotentWrapper().idempotentCreate(command, operationId, skuId,
                () -> DomainRegistry.skuService().create(
                        skuId,
                        command.getReferenceId(),
                        command.getDescription(),
                        command.getStorageOrder(),
                        command.getStorageActual(),
                        command.getPrice(),
                        command.getSales()
                ), Sku.class
        );
    }

    public SumPagedRep<Sku> skus(String queryParam, String pageParam, String skipCount) {
        return DomainRegistry.skuRepository().skusOfQuery(new SkuQuery(queryParam), new PageConfig(pageParam, 1000), new QueryConfig(skipCount));
    }

    public Optional<Sku> sku(String id) {
        return DomainRegistry.skuRepository().skuOfId(new SkuId(id));
    }

    @SubscribeForEvent
    @Transactional
    public void replace(String id, UpdateSkuCommand command, String changeId) {
        ApplicationServiceRegistry.idempotentWrapper().idempotent(command, changeId, (ignored) -> {
            SkuId SkuId = new SkuId(id);
            Optional<Sku> optionalSku = DomainRegistry.skuRepository().skuOfId(SkuId);
            if (optionalSku.isPresent()) {
                Sku sku = optionalSku.get();
                sku.replace(
                        command.getPrice(),
                        command.getDescription(),
                        command.getVersion()
                );
                DomainRegistry.skuRepository().add(sku);
            }
        }, Sku.class);
    }

    @SubscribeForEvent
    @Transactional
    public void removeById(String id, String changeId) {
        ApplicationServiceRegistry.idempotentWrapper().idempotent(id, changeId, (change) -> {
            SkuId SkuId = new SkuId(id);
            Optional<Sku> optionalSku = DomainRegistry.skuRepository().skuOfId(SkuId);
            if (optionalSku.isPresent()) {
                Sku sku = optionalSku.get();
                DomainRegistry.skuRepository().remove(sku);
            }
        }, Sku.class);
    }

    @SubscribeForEvent
    @Transactional
    public Set<String> removeByQuery(String queryParam, String changeId) {
        return ApplicationServiceRegistry.idempotentWrapper().idempotentDeleteByQuery(null, changeId, (change) -> {
            Set<Sku> skus = QueryUtility.getAllByQuery((query, page) -> DomainRegistry.skuRepository().skusOfQuery(query, page), new SkuQuery(queryParam));
            DomainRegistry.skuRepository().remove(skus);
            change.setRequestBody(skus);
            change.setDeletedIds(skus.stream().map(e -> e.getSkuId().getDomainId()).collect(Collectors.toSet()));
            change.setQuery(queryParam);
            return skus.stream().map(Sku::getSkuId).collect(Collectors.toSet());
        }, Sku.class);
    }

    @SubscribeForEvent
    @Transactional
    public void patch(String id, JsonPatch command, String changeId) {
        ApplicationServiceRegistry.idempotentWrapper().idempotent(command, changeId, (ignored) -> {
            SkuId SkuId = new SkuId(id);
            Optional<Sku> optionalCatalog = DomainRegistry.skuRepository().skuOfId(SkuId);
            if (optionalCatalog.isPresent()) {
                Sku filter = optionalCatalog.get();
                PatchSkuCommand beforePatch = new PatchSkuCommand(filter);
                PatchSkuCommand afterPatch = CommonDomainRegistry.customObjectSerializer().applyJsonPatch(command, beforePatch, PatchSkuCommand.class);
                filter.replace(
                        afterPatch.getPrice(),
                        afterPatch.getDescription(),
                        filter.getVersion()
                );
            }
        }, Sku.class);
    }

    @SubscribeForEvent
    @Transactional
    public void patchBatch(List<PatchCommand> commands, String changeId) {
        ApplicationServiceRegistry.idempotentWrapper().idempotent(commands, changeId, (ignored) -> {
            DomainRegistry.skuRepository().patchBatch(commands);
        }, Sku.class);
    }
}

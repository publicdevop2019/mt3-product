package com.mt.mall.application.sku;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.common.domain.model.idempotent.event.SkuChangeFailed;
import com.mt.common.domain.model.restful.PatchCommand;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.sql.builder.UpdateQueryBuilder;
import com.mt.mall.application.ApplicationServiceRegistry;
import com.mt.mall.application.sku.command.CreateSkuCommand;
import com.mt.mall.application.sku.command.PatchSkuCommand;
import com.mt.mall.application.sku.command.UpdateSkuCommand;
import com.mt.mall.domain.DomainRegistry;
import com.mt.mall.domain.model.product.event.ProductCreated;
import com.mt.mall.domain.model.product.event.ProductDeleted;
import com.mt.mall.domain.model.product.event.ProductPatchBatched;
import com.mt.mall.domain.model.product.event.ProductSkuUpdated;
import com.mt.mall.domain.model.sku.Sku;
import com.mt.mall.domain.model.sku.SkuId;
import com.mt.mall.domain.model.sku.SkuQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SkuApplicationService {
    @Value("${spring.application.name}")
    private String appName;
    @Autowired
    private PlatformTransactionManager transactionManager;


    @SubscribeForEvent
    @Transactional
    public String create(CreateSkuCommand command, String operationId) {
        SkuId skuId = new SkuId();
        return doCreate(command, operationId, skuId);
    }

    private String doCreate(CreateSkuCommand command, String operationId, SkuId skuId) {
        return ApplicationServiceRegistry.getIdempotentWrapper().idempotentCreate(command, operationId, skuId,
                () -> DomainRegistry.getSkuService().create(
                        skuId,
                        command.getReferenceId().getDomainId(),
                        command.getDescription(),
                        command.getStorageOrder(),
                        command.getStorageActual(),
                        command.getPrice(),
                        command.getSales()
                ), Sku.class
        );
    }

    public SumPagedRep<Sku> skus(String queryParam, String pageParam, String skipCount) {
        return DomainRegistry.getSkuRepository().skusOfQuery(new SkuQuery(queryParam, pageParam, skipCount));
    }

    public Optional<Sku> sku(String id) {
        return DomainRegistry.getSkuRepository().skuOfId(new SkuId(id));
    }

    @SubscribeForEvent
    @Transactional
    public void replace(String id, UpdateSkuCommand command, String changeId) {
        SkuId skuId = new SkuId(id);
        doReplace(command, changeId, skuId);
    }

    private void doReplace(UpdateSkuCommand command, String changeId, SkuId skuId) {
        ApplicationServiceRegistry.getIdempotentWrapper().idempotent(skuId, command, changeId, (ignored) -> {
            Optional<Sku> optionalSku = DomainRegistry.getSkuRepository().skuOfId(skuId);
            if (optionalSku.isPresent()) {
                Sku sku = optionalSku.get();
                sku.checkVersion(command.getVersion());
                sku.replace(
                        command.getPrice(),
                        command.getDescription()
                );
                DomainRegistry.getSkuRepository().add(sku);
            }
        }, Sku.class);
    }

    @SubscribeForEvent
    @Transactional
    public void removeById(String id, String changeId) {
        SkuId skuId = new SkuId(id);
        doRemoveById(changeId, skuId);
    }

    private void doRemoveById(String changeId, SkuId skuId) {
        ApplicationServiceRegistry.getIdempotentWrapper().idempotent(skuId, null, changeId, (change) -> {
            Optional<Sku> optionalSku = DomainRegistry.getSkuRepository().skuOfId(skuId);
            if (optionalSku.isPresent()) {
                Sku sku = optionalSku.get();
                DomainRegistry.getSkuRepository().remove(sku);
            }
        }, Sku.class);
    }

    @SubscribeForEvent
    @Transactional
    public Set<String> removeByQuery(String queryParam, String changeId) {
        return ApplicationServiceRegistry.getIdempotentWrapper().idempotentDeleteByQuery(queryParam, changeId, (change) -> {
            Set<Sku> skus = QueryUtility.getAllByQuery((query) -> DomainRegistry.getSkuRepository().skusOfQuery((SkuQuery) query), new SkuQuery(queryParam));
            DomainRegistry.getSkuRepository().remove(skus);
            change.setRequestBody(skus);
            change.setDeletedIds(skus.stream().map(e -> e.getSkuId().getDomainId()).collect(Collectors.toSet()));
            change.setQuery(queryParam);
            return skus.stream().map(Sku::getSkuId).collect(Collectors.toSet());
        }, Sku.class);
    }

    @SubscribeForEvent
    @Transactional
    public void patch(String id, JsonPatch command, String changeId) {
        SkuId skuId = new SkuId(id);
        ApplicationServiceRegistry.getIdempotentWrapper().idempotent(skuId, command, changeId, (ignored) -> {
            Optional<Sku> optionalCatalog = DomainRegistry.getSkuRepository().skuOfId(skuId);
            if (optionalCatalog.isPresent()) {
                Sku sku = optionalCatalog.get();
                PatchSkuCommand beforePatch = new PatchSkuCommand(sku);
                PatchSkuCommand afterPatch = CommonDomainRegistry.getCustomObjectSerializer().applyJsonPatch(command, beforePatch, PatchSkuCommand.class);
                sku.checkVersion(sku.getVersion());
                sku.replace(
                        afterPatch.getPrice(),
                        afterPatch.getDescription()
                );
            }
        }, Sku.class);
    }

    @SubscribeForEvent
    public void patchBatch(List<PatchCommand> commands, String changeId) {
        List<PatchCommand> patchCommands = List.copyOf(CommonDomainRegistry.getCustomObjectSerializer().deepCopyCollection(commands));
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        try {
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    ApplicationServiceRegistry.getIdempotentWrapper().idempotent(null, commands, changeId, (ignored) -> {
                        DomainRegistry.getSkuRepository().patchBatch(commands);
                    }, Sku.class);
                }
            });
        } catch (UpdateQueryBuilder.PatchCommandExpectNotMatchException ex) {
            log.debug("unable to update sku due to expect not match ", ex);
            //directly publish msg to stream
            SkuChangeFailed skuChangeFailed = new SkuChangeFailed(null, patchCommands);
            CommonDomainRegistry.getEventStreamService().next(appName, skuChangeFailed.isInternal(), skuChangeFailed.getTopic(), new StoredEvent(skuChangeFailed));
            throw ex;
        }
    }

    @SubscribeForEvent
    @Transactional
    public void handleChange(StoredEvent event) {
        ApplicationServiceRegistry.getIdempotentWrapper().idempotent(null, null, event.getId().toString(), (ignored) -> {
            if (ProductCreated.class.getName().equals(event.getName())) {
                ProductCreated deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), ProductCreated.class);
                create(deserialize.getCreateSkuCommands(), deserialize.getChangeId());
            }
            if (ProductSkuUpdated.class.getName().equals(event.getName())) {
                ProductSkuUpdated deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), ProductSkuUpdated.class);
                create(deserialize.getCreateSkuCommands(), deserialize.getChangeId());
                update(deserialize.getUpdateSkuCommands(), deserialize.getChangeId());
                remove(deserialize.getRemoveSkuCommands(), deserialize.getChangeId());
            }
            if (ProductDeleted.class.getName().equals(event.getName())) {
                ProductDeleted deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), ProductDeleted.class);
                remove(deserialize.getRemoveSkuCommands(), deserialize.getChangeId());
            }
            if (ProductPatchBatched.class.getName().equals(event.getName())) {
                ProductPatchBatched deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), ProductPatchBatched.class);
                log.debug("consuming ProductPatchBatched with id {}", deserialize.getId());
                patchBatch(deserialize.getPatchCommands(), deserialize.getChangeId());
            }
        }, Sku.class);
    }

    private void remove(Set<SkuId> removeSkuCommands, String changeId) {
        removeSkuCommands.forEach(e -> doRemoveById(changeId, e));
    }

    private void update(List<UpdateSkuCommand> updateSkuCommands, String changeId) {
        updateSkuCommands.forEach(e -> doReplace(e, changeId, e.getSkuId()));
    }

    private void create(List<CreateSkuCommand> createSkuCommands, String changId) {
        createSkuCommands.forEach(command -> doCreate(command, changId, command.getSkuId()));
    }
}

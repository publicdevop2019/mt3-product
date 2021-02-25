package com.mt.mall.application.tag;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain_event.SubscribeForEvent;
import com.mt.common.persistence.QueryConfig;
import com.mt.common.query.PageConfig;
import com.mt.common.query.QueryUtility;
import com.mt.common.sql.SumPagedRep;
import com.mt.mall.application.ApplicationServiceRegistry;
import com.mt.mall.application.tag.command.CreateTagCommand;
import com.mt.mall.application.tag.command.PatchTagCommand;
import com.mt.mall.application.tag.command.UpdateTagCommand;
import com.mt.mall.domain.DomainRegistry;
import com.mt.mall.domain.model.tag.Tag;
import com.mt.mall.domain.model.tag.TagId;
import com.mt.mall.domain.model.tag.TagQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TagApplicationService {

    @SubscribeForEvent
    @Transactional
    public String create(CreateTagCommand command, String operationId) {
        TagId tagId = DomainRegistry.tagRepository().nextIdentity();
        return ApplicationServiceRegistry.idempotentWrapper().idempotentCreate(command, operationId, tagId,
                () -> DomainRegistry.tagService().create(
                        tagId,
                        command.getName(),
                        command.getDescription(),
                        command.getMethod(),
                        command.getSelectValues(),
                        command.getType()
                ), Tag.class
        );
    }

    public SumPagedRep<Tag> tags(String queryParam, String pageParam, String skipCount) {
        return DomainRegistry.tagRepository().tagsOfQuery(new TagQuery(queryParam), new PageConfig(pageParam, 1000), new QueryConfig(skipCount));
    }

    public Optional<Tag> tag(String id) {
        return DomainRegistry.tagRepository().tagOfId(new TagId(id));
    }

    @SubscribeForEvent
    @Transactional
    public void replace(String id, UpdateTagCommand command, String changeId) {
        TagId tagId = new TagId(id);
        ApplicationServiceRegistry.idempotentWrapper().idempotent(tagId, command, changeId, (ignored) -> {
            Optional<Tag> optionalTag = DomainRegistry.tagRepository().tagOfId(tagId);
            if (optionalTag.isPresent()) {
                Tag tag = optionalTag.get();
                tag.replace(
                        command.getName(),
                        command.getDescription(),
                        command.getMethod(),
                        command.getSelectValues(),
                        command.getType()
                );
                DomainRegistry.tagRepository().add(tag);
            }
        }, Tag.class);
    }

    @SubscribeForEvent
    @Transactional
    public void removeById(String id, String changeId) {
        TagId tagId = new TagId(id);
        ApplicationServiceRegistry.idempotentWrapper().idempotent(tagId, null, changeId, (change) -> {
            Optional<Tag> optionalTag = DomainRegistry.tagRepository().tagOfId(tagId);
            if (optionalTag.isPresent()) {
                Tag tag = optionalTag.get();
                DomainRegistry.tagRepository().remove(tag);
            }
        }, Tag.class);
    }

    @SubscribeForEvent
    @Transactional
    public Set<String> removeByQuery(String queryParam, String changeId) {
        return ApplicationServiceRegistry.idempotentWrapper().idempotentDeleteByQuery(queryParam, changeId, (change) -> {
            Set<Tag> tags = QueryUtility.getAllByQuery((query, page) -> DomainRegistry.tagRepository().tagsOfQuery(query, page), new TagQuery(queryParam));
            DomainRegistry.tagRepository().remove(tags);
            change.setRequestBody(tags);
            change.setDeletedIds(tags.stream().map(e -> e.getTagId().getDomainId()).collect(Collectors.toSet()));
            change.setQuery(queryParam);
            return tags.stream().map(Tag::getTagId).collect(Collectors.toSet());
        }, Tag.class);
    }

    @SubscribeForEvent
    @Transactional
    public void patch(String id, JsonPatch command, String changeId) {
        TagId tagId = new TagId(id);
        ApplicationServiceRegistry.idempotentWrapper().idempotent(tagId, command, changeId, (ignored) -> {
            Optional<Tag> optionalCatalog = DomainRegistry.tagRepository().tagOfId(tagId);
            if (optionalCatalog.isPresent()) {
                Tag tag = optionalCatalog.get();
                PatchTagCommand beforePatch = new PatchTagCommand(tag);
                PatchTagCommand afterPatch = CommonDomainRegistry.getCustomObjectSerializer().applyJsonPatch(command, beforePatch, PatchTagCommand.class);
                tag.replace(
                        afterPatch.getName(),
                        afterPatch.getDescription(),
                        afterPatch.getMethod(),
                        afterPatch.getSelectValues(),
                        afterPatch.getType()
                );
                DomainRegistry.tagRepository().add(tag);
            }
        }, Tag.class);
    }

}

package com.hw.aggregate.product.model;

import com.hw.shared.Auditable;
import com.hw.shared.rest.IdBasedEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Data
@Entity
@Table(name = "biz_ref_tag_map")
@NoArgsConstructor
@Slf4j
public class TagReferenceMap extends Auditable implements IdBasedEntity {
    @Id
    private Long id;
    private Long referenceId;
    private Long tagId;
    private String tagValue;
}

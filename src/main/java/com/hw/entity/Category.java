package com.hw.entity;

import com.hw.shared.Auditable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "Category")
@Data
@EqualsAndHashCode(callSuper = false)
public class Category extends Auditable {

    @Id
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String title;

}

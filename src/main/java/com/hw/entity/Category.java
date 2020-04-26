package com.hw.entity;

import com.hw.shared.Auditable;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "Category")
@SequenceGenerator(name = "categoryId_gen", sequenceName = "categoryId_gen", initialValue = 100)
@Data
@EqualsAndHashCode(callSuper=false)
public class Category extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "categoryId_gen")
    @Setter(AccessLevel.NONE)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String title;

}

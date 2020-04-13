package com.hw.entity;

import com.hw.converter.MapConverter;
import com.hw.shared.Auditable;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.persistence.*;
import java.util.Map;

@Entity
@Table
@Data
@SequenceGenerator(name = "changeId_gen", sequenceName = "changeId_gen", initialValue = 100)
public class ChangeRecord extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "changeId_gen")
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(nullable = false)
    private String optToken;

    @Column(nullable = false)
    private String changeField;

    @Column(nullable = false)
    @Convert(converter = MapConverter.class)
    private Map<String, String> changeValues;

    @Column(nullable = false)
    private String changeType;
}

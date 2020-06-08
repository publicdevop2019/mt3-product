package com.hw.entity;

import com.hw.converter.MapConverter;
import com.hw.shared.Auditable;
import lombok.Data;

import javax.persistence.*;
import java.util.Map;

@Entity
@Table
@Data
public class TransactionRecord extends Auditable {
    @Id
    private Long id;

    @Column(nullable = false, unique = true)
    private String transactionId;

    @Column(nullable = false)
    private String changeField;

    @Column(nullable = false)
    @Convert(converter = MapConverter.class)
    private Map<String, String> changeValues;

    @Column(nullable = false)
    private String changeType;
}

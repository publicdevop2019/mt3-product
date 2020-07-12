package com.hw.aggregate.product.model;

import com.hw.shared.Auditable;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
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

    @Column(nullable = false,length = 100000)
    private ArrayList<StorageChangeDetail> changeValues;

    @Column(nullable = false)
    private String changeType;
}

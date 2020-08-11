package com.hw.aggregate.product.model;

import com.hw.shared.Auditable;
import com.hw.shared.sql.PatchCommand;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.ArrayList;

@Entity
@Table
@Data
public class ChangeRecord extends Auditable {
    @Id
    private Long id;

    @Column(nullable = false, unique = true)
    private String changeId;

    @Column(nullable = false, length = 100000)
    private ArrayList<PatchCommand> patchCommands;

}

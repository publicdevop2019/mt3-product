package com.hw.aggregate.product.command;

import lombok.Data;

@Data
public class DeleteProductAdminCommand {
    private Long id;

    public DeleteProductAdminCommand(Long productDetailId) {
        this.id = productDetailId;
    }
}

package com.hw.aggregate.sku;

import com.hw.aggregate.sku.model.BizSku;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BizSkuRepo extends JpaRepository<BizSku, Long> {
}

package com.hw.aggregate.sku;

import com.hw.aggregate.product.model.Product;
import com.hw.aggregate.sku.model.BizSku;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestHeader;

@Repository
public interface BizSkuRepo extends JpaRepository<BizSku, Long> {
}

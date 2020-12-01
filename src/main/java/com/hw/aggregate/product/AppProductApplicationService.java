package com.hw.aggregate.product;

import com.hw.aggregate.product.model.Product;
import com.hw.aggregate.product.model.ProductQueryRegistry;
import com.hw.aggregate.product.representation.AppProductCardRep;
import com.hw.aggregate.sku.AppBizSkuApplicationService;
import com.hw.shared.rest.RoleBasedRestfulService;
import com.hw.shared.rest.VoidTypedClass;
import com.hw.shared.sql.PatchCommand;
import com.hw.shared.sql.RestfulQueryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.hw.aggregate.product.representation.AdminProductRep.ADMIN_REP_SKU_LITERAL;

@Slf4j
@Service
public class AppProductApplicationService extends RoleBasedRestfulService<Product, AppProductCardRep, Void, VoidTypedClass> {
    {
        entityClass = Product.class;
        role = RestfulQueryRegistry.RoleEnum.APP;
    }

    @Autowired
    private AppBizSkuApplicationService appBizSkuApplicationService;
    @Autowired
    private ProductQueryRegistry productQueryRegistry;

    @Override
    public AppProductCardRep getEntitySumRepresentation(Product product) {
        return new AppProductCardRep(product, appBizSkuApplicationService);
    }

    @Override
    public Integer patchBatch(List<PatchCommand> commands, String changeId) {
        List<PatchCommand> skuChange = commands.stream().filter(e -> e.getPath().contains("/" + ADMIN_REP_SKU_LITERAL)).collect(Collectors.toList());
        List<PatchCommand> productChange = commands.stream().filter(e -> !e.getPath().contains("/" + ADMIN_REP_SKU_LITERAL)).collect(Collectors.toList());
        Integer execute = transactionTemplate.execute(transactionStatus -> {
            if (!skuChange.isEmpty())
                appBizSkuApplicationService.patchBatch(Product.convertToSkuCommands(skuChange, this), changeId);
            if (!productChange.isEmpty())
                return super.patchBatch(productChange, changeId);
            return 0;
        });
        cleanUpAllCache();
        appBizSkuApplicationService.cleanUpAllCache();//need ids to clear cache more accurately
        return execute;
    }

}


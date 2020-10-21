package com.hw.aggregate.product;

import com.hw.aggregate.product.model.Product;
import com.hw.aggregate.product.representation.PublicProductCardRep;
import com.hw.aggregate.product.representation.PublicProductRep;
import com.hw.aggregate.sku.AppBizSkuApplicationService;
import com.hw.aggregate.tag.AppBizTagApplicationService;
import com.hw.shared.rest.DefaultRoleBasedRestfulService;
import com.hw.shared.rest.VoidTypedClass;
import com.hw.shared.sql.RestfulQueryRegistry;
import com.hw.shared.sql.SumPagedRep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

import static com.hw.aggregate.product.model.PublicProductSelectQueryBuilder.PUBLIC_ATTR;

@Slf4j
@Service
public class PublicProductApplicationService extends DefaultRoleBasedRestfulService<Product, PublicProductCardRep, PublicProductRep, VoidTypedClass> {


    public static final String STRING_DEL = ":";
    @Autowired
    private AppBizTagApplicationService appBizAttributeApplicationService;

    @Autowired
    private AppBizSkuApplicationService appBizSkuApplicationService;

    @Override
    public SumPagedRep<PublicProductCardRep> readByQuery(String query, String page, String config) {
        // for attr:835602958278656-女.男$835604723556352-粉色.白色.灰色, bcz jpa does not support union type hence break it into multiple query
        if (Arrays.stream(query.split(",")).anyMatch(e -> e.indexOf(PUBLIC_ATTR + STRING_DEL) == 0)) {
            Set<String> collect = Arrays.stream(query.split(",")).filter(e -> !e.contains(PUBLIC_ATTR + STRING_DEL)).collect(Collectors.toSet());
            String join = String.join(",", collect);
            String first = Arrays.stream(query.split(",")).filter(e -> e.indexOf(PUBLIC_ATTR + STRING_DEL) == 0).findFirst().get();
            if (first.contains(".")) {
                HashMap<Integer, HashSet<String>> stringIntegerHashMap = new HashMap<>();
                String replace = first.replace(PUBLIC_ATTR + STRING_DEL, "");
                String[] split = replace.split("\\$");
                int i = 0;
                for (String str : split) {
                    stringIntegerHashMap.put(i, new HashSet<>());
                    String[] split1 = str.split("-");
                    String s = split1[0];
                    String[] split2 = split1[1].split("\\.");
                    for (String str1 : split2) {
                        stringIntegerHashMap.get(i).add(s + "-" + str1);
                    }
                    i++;
                }

                //0 835602958278656-女 835602958278656-男
                //1 835604723556352-粉色 835604723556352-白色 835604723556352-灰色
                //2 835604723556357-S 835604723556357-M
                int b = 0;
                for (int a = 0; a < i - 1; a++) {
                    HashSet<String> output = new HashSet<>();
                    HashSet<String> set = stringIntegerHashMap.get(a);
                    int i1 = b + 1;
                    set.forEach(str -> {
                        HashSet<String> strings = stringIntegerHashMap.get(i1);
                        strings.forEach(k2 -> {
                            output.add(str + "$" + k2);
                        });
                    });
                    stringIntegerHashMap.get(i1).clear();
                    stringIntegerHashMap.get(i1).addAll(output);
                }
                HashSet<String> strings = stringIntegerHashMap.get(i - 1);
                HashSet<String> finalOutput = new HashSet<>();
                strings.forEach(e -> {
                    if ("".equals(join)) {
                        finalOutput.add(PUBLIC_ATTR + STRING_DEL + e);
                    } else {
                        finalOutput.add(String.join(",", PUBLIC_ATTR + STRING_DEL + e, join));
                    }
                });
                SumPagedRep<PublicProductCardRep> out = new SumPagedRep<>();
                finalOutput.forEach(e -> {
                    SumPagedRep<PublicProductCardRep> publicProductCardRepSumPagedRep = super.readByQuery(e, null, config);
                    List<PublicProductCardRep> data = out.getData();
                    data.addAll(publicProductCardRepSumPagedRep.getData());
                    out.setTotalItemCount(out.getTotalItemCount() + publicProductCardRepSumPagedRep.getTotalItemCount());
                });
                return out;
            } else {
                return super.readByQuery(query, page, config);
            }
        }

        return super.readByQuery(query, page, config);
    }

    @PostConstruct
    private void setUp() {
        entityClass = Product.class;
        role = RestfulQueryRegistry.RoleEnum.PUBLIC;
    }

    @Override
    public Product replaceEntity(Product product, Object command) {
        return null;
    }

    @Override
    public PublicProductCardRep getEntitySumRepresentation(Product product) {
        return new PublicProductCardRep(product);
    }

    @Override
    public PublicProductRep getEntityRepresentation(Product product) {
        return new PublicProductRep(product, appBizAttributeApplicationService, appBizSkuApplicationService);
    }

    @Override
    protected Product createEntity(long id, Object command) {
        return null;
    }

    @Override
    public void preDelete(Product product) {

    }

    @Override
    public void postDelete(Product product) {

    }

    @Override
    protected void prePatch(Product product, Map<String, Object> params, VoidTypedClass middleLayer) {

    }

    @Override
    protected void postPatch(Product product, Map<String, Object> params, VoidTypedClass middleLayer) {

    }
}


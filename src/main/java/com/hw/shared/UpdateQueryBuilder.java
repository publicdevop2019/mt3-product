package com.hw.shared;

import com.hw.aggregate.product.model.PatchCommand;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.hw.aggregate.product.model.ProductDetail.ID_LITERAL;

public abstract class UpdateQueryBuilder<T> {
    protected EntityManager em;

    /**
     * sample :[
     * {op:'replace',path:'/0001/name',value:'foo'},
     * {op:'replace',path:'/0002/name',value:'foo'}
     * {op:'replace',path:'/0003/name',value:'foo'}
     * {op:'replace',path:'/0004/name',value:'foo'}
     * {op:'replace',path:'/0003/title',value:'bar'}
     * {op:'replace',path:'/0002/title',value:'xyz'}
     * {op:'replace',path:'/0002/address',value:'zoo'}
     * ]
     */
    public Integer update(List<PatchCommand> commands, Class<T> clazz) {
        HashMap<PatchCommand, List<String>> jsonPatchCommandListHashMap = new HashMap<>();

        commands.forEach(e -> {
            String s = parseId(e.getPath());
            e.setPath(removeId(e.getPath()));
            if (jsonPatchCommandListHashMap.containsKey(e)) {
                List<String> strings = jsonPatchCommandListHashMap.get(e);
                strings.add(s);
            } else {
                ArrayList<String> strings = new ArrayList<>();
                strings.add(s);
                jsonPatchCommandListHashMap.put(e, strings);
            }
        });
        List<CriteriaUpdate<T>> criteriaUpdates = jsonPatchCommandListHashMap.keySet().stream().map(e -> {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaUpdate<T> criteriaUpdate = cb.createCriteriaUpdate(clazz);
            Root<T> root = criteriaUpdate.from(clazz);
            List<Predicate> results = new ArrayList<>();
            for (String str : jsonPatchCommandListHashMap.get(e)) {
                results.add(cb.equal(root.get(ID_LITERAL), Long.parseLong(str)));
            }
            Predicate or = cb.or(results.toArray(new Predicate[0]));
            if (or != null)
                criteriaUpdate.where(or);
            setUpdateValue(criteriaUpdate, e);
            return criteriaUpdate;
        }).collect(Collectors.toList());
        return criteriaUpdates.stream().map(e -> em.createQuery(e).executeUpdate()).reduce(0, Integer::sum);
    }

    private String removeId(String path) {
        String[] split = path.split("/");
        List<String> collect = Arrays.stream(split).collect(Collectors.toList());
        collect.remove(0);
        collect.remove(0);
        return "/" + String.join("/", collect);
    }

    private String parseId(String path) {
        String[] split = path.split("/");
        return split[1];
    }

    protected abstract void setUpdateValue(CriteriaUpdate<T> criteriaUpdate, PatchCommand operationLike);

}

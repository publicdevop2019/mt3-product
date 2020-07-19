package com.hw.aggregate.filter;

import com.hw.aggregate.filter.command.CreateBizFilterCommand;
import com.hw.aggregate.filter.command.UpdateBizFilterCommand;
import com.hw.aggregate.filter.model.BizFilter;
import com.hw.aggregate.filter.representation.BizFilterAdminRepresentation;
import com.hw.aggregate.filter.representation.BizFilterAdminSummaryRepresentation;
import com.hw.aggregate.filter.representation.BizFilterCreatedRepresentation;
import com.hw.aggregate.filter.representation.BizFilterCustomerRepresentation;
import com.hw.shared.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
public class BizFilterApplicationService {
    @Autowired
    BizFilterRepository bizFilterRepository;
    @Autowired
    IdGenerator idGenerator;
    @Autowired
    private EntityManager entityManager;

    public BizFilterCreatedRepresentation create(CreateBizFilterCommand command) {
        BizFilter bizFilter = BizFilter.create(idGenerator.getId(), command, bizFilterRepository);
        return new BizFilterCreatedRepresentation(bizFilter);
    }

    public void update(Long filterId, UpdateBizFilterCommand command) {
        BizFilter.update(filterId, command, bizFilterRepository);
    }

    public void delete(Long filterId) {
        BizFilter.delete(filterId, bizFilterRepository);
    }

    public BizFilterAdminRepresentation getById(Long filterId) {
        BizFilter read = BizFilter.read(filterId, bizFilterRepository);
        return new BizFilterAdminRepresentation(read);
    }

    public BizFilterAdminSummaryRepresentation getAll() {
        List<BizFilter> all = bizFilterRepository.findAll();
        return new BizFilterAdminSummaryRepresentation(all);
    }

    public BizFilterCustomerRepresentation getByCatalog(String catalog) {
        List<Object[]> resultList = entityManager.createNativeQuery("SELECT id, filter_items" +
                " FROM biz_filter pd WHERE pd.linked_catalog LIKE '%" + catalog + "%'")
                .getResultList();

        BizFilter byCatalog = new BizFilter();
        BigInteger bigInteger = (BigInteger) resultList.get(0)[0];
        byte[] bytes = (byte[]) resultList.get(0)[1];
        byCatalog.setId((bigInteger).longValue());
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInput in = new ObjectInputStream(bis)
        ) {
            Object o = in.readObject();
            byCatalog.setFilterItems((ArrayList<BizFilter.BizFilterItem>) o);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new BizFilterCustomerRepresentation(byCatalog);
    }
}

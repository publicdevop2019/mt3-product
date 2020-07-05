package com.hw.aggregate.attribute.model;

import com.hw.aggregate.attribute.BizAttributeRepository;
import com.hw.aggregate.attribute.command.CreateBizAttributeCommand;
import com.hw.aggregate.attribute.command.UpdateBizAttributeCommand;
import com.hw.aggregate.attribute.exception.BizAttributeNotFoundException;
import com.hw.shared.Auditable;
import com.hw.shared.StringSetConverter;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Optional;
import java.util.Set;

@Data
@Entity
@Table(name = "biz_attribute")
@NoArgsConstructor
public class BizAttribute extends Auditable {
    @Id
    private Long id;
    private String name;
    private String description;
    @Convert(converter = AttributeMethod.DBConverter.class)
    private AttributeMethod method;
    @Convert(converter = StringSetConverter.class)
    private Set<String> selectValues;
    @Convert(converter = BizAttributeType.DBConverter.class)
    private BizAttributeType type;

    public static BizAttribute create(Long id, CreateBizAttributeCommand command, BizAttributeRepository attributeRepository) {
        BizAttribute attribute = new BizAttribute(id, command);
        attributeRepository.save(attribute);
        return attribute;
    }

    private BizAttribute(Long id, CreateBizAttributeCommand command) {
        this.id = id;
        this.name = command.getName();
        this.description = command.getDescription();
        this.method = command.getMethod();
        this.selectValues = command.getSelectValues();
        this.type = command.getType();
    }

    public static void update(Long attributeId, UpdateBizAttributeCommand command, BizAttributeRepository attributeRepository) {
        BizAttribute read = read(attributeId, attributeRepository);
        read.setName(command.getName());
        read.setDescription(command.getDescription());
        read.setMethod(command.getMethod());
        read.setSelectValues(command.getSelectValues());
        read.setType(command.getType());
        attributeRepository.save(read);
    }

    public static void delete(Long attributeId, BizAttributeRepository attributeRepository) {
        BizAttribute read = read(attributeId, attributeRepository);
        attributeRepository.delete(read);
    }

    public static BizAttribute read(Long attributeId, BizAttributeRepository attributeRepository) {
        Optional<BizAttribute> byId = attributeRepository.findById(attributeId);
        if (byId.isEmpty())
            throw new BizAttributeNotFoundException();
        return byId.get();
    }
}

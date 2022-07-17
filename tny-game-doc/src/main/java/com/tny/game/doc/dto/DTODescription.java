package com.tny.game.doc.dto;

import com.tny.game.doc.*;
import com.tny.game.doc.holder.*;

import java.util.*;

import static com.tny.game.common.utils.StringAide.*;

public class DTODescription extends ClassDescription {

    private final String des;

    private final Object id;

    private final boolean push;

    private final List<FieldDescription> dtoFieldList;

    public DTODescription(DTODocHolder holder, TypeFormatter typeFormatter) {
        super(holder);
        this.id = holder.getId();
        this.des = holder.getDTODoc().value();
        this.push = holder.getDTODoc().push();
        Map<Integer, FieldDescription> fieldMap = new HashMap<>();
        List<FieldDescription> fieldList = new ArrayList<>();
        for (DocVar fieldDocHolder : holder.getFieldList()) {
            try {
                FieldDescription fieldDescription = new FieldDescription(fieldDocHolder, typeFormatter);
                fieldList.add(fieldDescription);
                FieldDescription old = fieldMap.put(fieldDescription.getFieldId(), fieldDescription);
                if (old != null) {
                    throw new IllegalArgumentException(format("{} 类 {} 与 {} 字段 ID 都为 {}",
                            holder.getEntityClass(), fieldDescription.getFieldName(), old.getFieldName(), fieldDescription.getFieldId()));
                }
            } catch (Throwable e) {
                throw new IllegalArgumentException(format("{} 类 解析异常", holder.getEntityClass()), e);
            }
        }
        this.dtoFieldList = Collections.unmodifiableList(fieldList);
    }

    public boolean isPush() {
        return push;
    }

    public String getDes() {
        return des;
    }

    public Object getId() {
        return id;
    }

    public List<FieldDescription> getDtoFieldList() {
        return dtoFieldList;
    }

    @Override
    public String toString() {
        return "DTODescription{" +
                "className='" + getClassName() + '\'' +
                ", des='" + des + '\'' +
                '}';
    }

}

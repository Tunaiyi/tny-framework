package com.tny.game.suite.base.dto;

import com.tny.game.doc.annotation.*;
import com.tny.game.protoex.annotations.*;
import com.tny.game.suite.*;

import java.util.*;


@ProtoEx(SuiteProtoIDs.DOUBLE_LIST_DTO)
@DTODoc("通用Double List DTO")
public class DoubleListDTO {

    @VarDoc("值列表")
    @ProtoExField(1)
    protected List<Double> values = new ArrayList<>();

    public DoubleListDTO() {
    }

    public static DoubleListDTO values2DTO(List<Double> values) {
        DoubleListDTO dto = new DoubleListDTO();
        dto.values.addAll(values);
        return dto;
    }

    public static DoubleListDTO values2DTO(Double... values) {
        DoubleListDTO dto = new DoubleListDTO();
        dto.values.addAll(Arrays.asList(values));
        return dto;
    }

    public List<Double> getValues() {
        return values;
    }

    @Override
    public String toString() {
        String text = "";
        for (Object value : values)
            text += value;
        return text;
    }
}

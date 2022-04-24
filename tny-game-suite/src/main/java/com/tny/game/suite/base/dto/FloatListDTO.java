package com.tny.game.suite.base.dto;

import com.tny.game.doc.annotation.*;
import com.tny.game.protoex.annotations.*;
import com.tny.game.suite.*;

import java.util.*;

@ProtoEx(SuiteProtoIDs.FLOAT_LIST_DTO)
@DTODoc("通用Float List DTO")
public class FloatListDTO {

    @VarDoc("值列表")
    @ProtoExField(1)
    protected List<Float> values = new ArrayList<>();

    public FloatListDTO() {
    }

    public static FloatListDTO values2DTO(List<Float> values) {
        FloatListDTO dto = new FloatListDTO();
        dto.values.addAll(values);
        return dto;
    }

    public static FloatListDTO values2DTO(Float... values) {
        FloatListDTO dto = new FloatListDTO();
        dto.values.addAll(Arrays.asList(values));
        return dto;
    }

    public List<Float> getValues() {
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

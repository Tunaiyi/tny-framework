package com.tny.game.suite.base.dto;

import com.tny.game.doc.annotation.*;
import com.tny.game.protoex.annotations.*;
import com.tny.game.suite.*;

import java.util.*;

@ProtoEx(SuiteProtoIDs.LONG_LIST_DTO)
@DTODoc("通用Long List DTO")
public class LongListDTO {

    @VarDoc("值列表")
    @ProtoExField(1)
    protected List<Long> values = new ArrayList<>();

    public LongListDTO() {
    }

    public static LongListDTO values2DTO(List<Long> values) {
        LongListDTO dto = new LongListDTO();
        dto.values.addAll(values);
        return dto;
    }

    public static LongListDTO values2DTO(Long... values) {
        LongListDTO dto = new LongListDTO();
        dto.values.addAll(Arrays.asList(values));
        return dto;
    }

    public List<Long> getValues() {
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

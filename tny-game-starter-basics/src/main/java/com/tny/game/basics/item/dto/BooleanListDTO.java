package com.tny.game.basics.item.dto;

import com.tny.game.basics.item.*;
import com.tny.game.doc.annotation.*;
import com.tny.game.protoex.annotations.*;

import java.util.*;

@ProtoEx(BasicsProtoIDs.BOOL_LIST_DTO)
@DTODoc("通用Boolean List DTO")
public class BooleanListDTO {

    @VarDoc("值列表")
    @ProtoExField(1)
    protected List<Boolean> values = new ArrayList<>();

    public BooleanListDTO() {
    }

    public static BooleanListDTO values2DTO(List<Boolean> values) {
        BooleanListDTO dto = new BooleanListDTO();
        dto.values.addAll(values);
        return dto;
    }

    public static BooleanListDTO values2DTO(Boolean... values) {
        BooleanListDTO dto = new BooleanListDTO();
        dto.values.addAll(Arrays.asList(values));
        return dto;
    }

    public List<Boolean> getValues() {
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

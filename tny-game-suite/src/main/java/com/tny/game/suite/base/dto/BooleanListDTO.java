package com.tny.game.suite.base.dto;

import com.tny.game.doc.annotation.DTODoc;
import com.tny.game.doc.annotation.VarDoc;
import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.protoex.annotations.ProtoExField;
import com.tny.game.suite.SuiteProtoIDs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@ProtoEx(SuiteProtoIDs.BOOL_LIST_DTO)
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

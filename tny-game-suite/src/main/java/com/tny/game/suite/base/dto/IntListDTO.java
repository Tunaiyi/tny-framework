package com.tny.game.suite.base.dto;

import com.tny.game.doc.annotation.DTODoc;
import com.tny.game.doc.annotation.VarDoc;
import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.protoex.annotations.ProtoExField;
import com.tny.game.suite.SuiteProtoIDs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@ProtoEx(SuiteProtoIDs.INT_LIST_DTO)
@DTODoc("通用Int List DTO")
public class IntListDTO {

    @VarDoc("值列表")
    @ProtoExField(1)
    protected List<Integer> values = new ArrayList<>();

    public IntListDTO() {
    }

    public static IntListDTO values2DTO(List<Integer> values) {
        IntListDTO dto = new IntListDTO();
        dto.values.addAll(values);
        return dto;
    }

    public static IntListDTO values2DTO(Integer... values) {
        IntListDTO dto = new IntListDTO();
        dto.values.addAll(Arrays.asList(values));
        return dto;
    }

    public List<Integer> getValues() {
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

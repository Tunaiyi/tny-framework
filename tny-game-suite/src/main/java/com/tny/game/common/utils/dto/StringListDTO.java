package com.tny.game.common.utils.dto;

import com.tny.game.doc.annotation.DTODoc;
import com.tny.game.doc.annotation.VarDoc;
import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.protoex.annotations.ProtoExField;
import com.tny.game.suite.SuiteProtoIDs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@ProtoEx(SuiteProtoIDs.STRING_LIST_DTO)
@DTODoc("通用String List DTO")
public class StringListDTO {

    @VarDoc("值列表")
    @ProtoExField(1)
    protected List<String> values = new ArrayList<>();

    public StringListDTO() {
    }

    public static StringListDTO values2DTO(List<String> values) {
        StringListDTO dto = new StringListDTO();
        dto.values.addAll(values);
        return dto;
    }

    public static StringListDTO values2DTO(String... values) {
        StringListDTO dto = new StringListDTO();
        dto.values.addAll(Arrays.asList(values));
        return dto;
    }

    public List<String> getValues() {
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

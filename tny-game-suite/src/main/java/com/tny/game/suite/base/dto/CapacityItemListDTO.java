package com.tny.game.suite.base.dto;

import com.tny.game.doc.annotation.DTODoc;
import com.tny.game.doc.annotation.VarDoc;
import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.protoex.annotations.ProtoExField;
import com.tny.game.suite.SuiteProtoIDs;
import com.tny.game.suite.base.capacity.CapacitySupplier;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@ProtoEx(SuiteProtoIDs.CAPACITY_ITEM_LIST_DTO)
@DTODoc(value = "游戏能力相关对象列表DTO")
public class CapacityItemListDTO {

    @VarDoc("能力相关对象列表")
    @ProtoExField(1)
    private List<CapacityItemDTO> capacityItems;

    public static CapacityItemListDTO suppliers2DTO(Collection<? extends CapacitySupplier> suppliers) {
        CapacityItemListDTO dto = new CapacityItemListDTO();
        dto.capacityItems = suppliers.stream()
                .map(CapacityItemDTO::supplier2DTO)
                .collect(Collectors.toList());
        return dto;
    }


}

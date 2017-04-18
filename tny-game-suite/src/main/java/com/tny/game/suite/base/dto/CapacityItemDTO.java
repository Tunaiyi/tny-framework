package com.tny.game.suite.base.dto;


import com.tny.game.doc.annotation.DTODoc;
import com.tny.game.doc.annotation.VarDoc;
import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.protoex.annotations.ProtoExField;
import com.tny.game.suite.SuiteProtoIDs;
import com.tny.game.suite.base.capacity.CapacitySupplier;
import com.tny.game.suite.base.capacity.ComboCapacitySupplier;
import com.tny.game.suite.base.capacity.TimeoutCapacitySupplier;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@ProtoEx(SuiteProtoIDs.CAPACITY_ITEM_DTO)
@DTODoc(value = "游戏能力相关对象DTO")
public class CapacityItemDTO {

    @VarDoc("提供者ID")
    @ProtoExField(1)
    private long id;

    @VarDoc("能力值列表")
    @ProtoExField(2)
    private List<CapacityDTO> capacities;

    @VarDoc("能力值提供器依赖的能力值提供器列表")
    @ProtoExField(3)
    private List<Long> dependSuppliers;

    @VarDoc("是否有时间限制")
    @ProtoExField(4)
    private boolean timeout;

    @VarDoc("剩余时间")
    @ProtoExField(5)
    private Long remainTime;

    public static CapacityItemDTO supplier2DTO(CapacitySupplier supplier) {
        CapacityItemDTO dto = new CapacityItemDTO();
        dto.id = supplier.getID();
        if (supplier instanceof ComboCapacitySupplier)
            dto.dependSuppliers = combo2IDs((ComboCapacitySupplier) supplier);
        else
            dto.capacities = supplier.getAllCapacityValue().entrySet().stream()
                    .map(entry -> CapacityDTO.value2DTO(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList());
        if (supplier instanceof TimeoutCapacitySupplier) {
            TimeoutCapacitySupplier timeoutSupplier = (TimeoutCapacitySupplier) supplier;
            long remain = timeoutSupplier.countRemainTime(System.currentTimeMillis());
            if (remain >= 0) {
                dto.timeout = true;
                dto.remainTime = remain;
            }
        }
        return dto;
    }

    public static List<CapacityItemDTO> suppliers2DTOs(Collection<? extends CapacitySupplier> suppliers) {
        return suppliers.stream()
                .map(CapacityItemDTO::supplier2DTO)
                .collect(Collectors.toList());
    }

    public List<CapacityDTO> getCapacities() {
        return capacities;
    }

    private static List<Long> combo2IDs(ComboCapacitySupplier supplier) {
        return supplier.dependSuppliers().stream().map(CapacitySupplier::getID).collect(Collectors.toList());
    }

}

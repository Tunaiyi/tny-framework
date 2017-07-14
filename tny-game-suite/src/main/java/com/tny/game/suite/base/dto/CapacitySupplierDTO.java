package com.tny.game.suite.base.dto;


import com.tny.game.doc.annotation.DTODoc;
import com.tny.game.doc.annotation.VarDoc;
import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.protoex.annotations.ProtoExField;
import com.tny.game.suite.SuiteProtoIDs;
import com.tny.game.suite.base.capacity.CapacityGoal;
import com.tny.game.suite.base.capacity.CapacitySupplier;
import com.tny.game.suite.base.capacity.ComboCapacitySupplier;
import com.tny.game.suite.base.capacity.TimeoutCapacitySupplier;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@ProtoEx(SuiteProtoIDs.CAPACITY_SUPPLIER_DTO)
@DTODoc(value = "游戏能力相关对象DTO")
public class CapacitySupplierDTO {

    @VarDoc("提供者ID")
    @ProtoExField(1)
    private long id;

    @VarDoc("提供者itemID")
    @ProtoExField(7)
    private int itemID;

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

    @VarDoc("是不是目标")
    @ProtoExField(6)
    private boolean goal;

    public static <T extends CapacitySupplier & CapacityGoal> CapacitySupplierDTO goalSupplier2DTO(T g) {
        CapacitySupplierDTO dto = supplier2DTO(g);
        dto.goal = true;
        dto.dependSuppliers = g.suppliers().stream()
                .filter(CapacitySupplier::isSupplying)
                .map(CapacitySupplier::getID)
                .collect(Collectors.toList());
        return dto;
    }

    public static CapacitySupplierDTO supplier2DTO(CapacitySupplier supplier) {
        CapacitySupplierDTO dto = new CapacitySupplierDTO();
        dto.id = supplier.getID();
        dto.itemID = supplier.getItemID();
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

    public static List<CapacitySupplierDTO> suppliers2DTOs(Collection<? extends CapacitySupplier> suppliers) {
        return suppliers.stream()
                .map(CapacitySupplierDTO::supplier2DTO)
                .collect(Collectors.toList());
    }

    public List<CapacityDTO> getCapacities() {
        return capacities;
    }

    private static List<Long> combo2IDs(ComboCapacitySupplier supplier) {
        return supplier.dependSuppliers().stream().filter(CapacitySupplier::isSupplying).map(CapacitySupplier::getID).collect(Collectors.toList());
    }

}

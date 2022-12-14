package com.tny.game.suite.base.dto;

import com.google.common.collect.*;
import com.tny.game.doc.annotation.*;
import com.tny.game.protoex.annotations.*;
import com.tny.game.suite.*;
import com.tny.game.suite.base.capacity.*;

import java.util.*;
import java.util.stream.Collectors;

import static com.tny.game.common.utils.ObjectAide.*;

@ProtoEx(SuiteProtoIDs.CAPACITY_SUPPLIER_DTO)
@DTODoc(value = "游戏能力相关对象DTO")
public class CapacitySupplierDTO {

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

    @VarDoc("是不是目标")
    @ProtoExField(6)
    private boolean goal;

    @VarDoc("提供者itemId")
    @ProtoExField(7)
    private int itemId;

    @VarDoc("能力值组")
    @ProtoExField(8)
    private Set<Integer> capacityGroups;

    private static void initComboSupplier(CapacitySupplierDTO dto, ComboCapacitySupplier supplier) {
        Set<CapacityGroup> capacityGroups = new HashSet<>();
        dto.dependSuppliers = supplier.dependSuppliers().stream()
                .filter(CapacitySupplier::isSupplying)
                .peek(s -> capacityGroups.addAll(s.getAllCapacityGroups()))
                .map(CapacitySupplier::getId)
                .collect(Collectors.toList());
        dto.capacityGroups = supplier.getAllCapacityGroups().stream()
                .map(CapacityGroup::getId)
                .collect(Collectors.toSet());
    }

    public static CapacitySupplierDTO supplier2DTO(CapacitySupplier supplier) {
        CapacitySupplierDTO dto = new CapacitySupplierDTO();
        dto.id = supplier.getId();
        dto.itemId = supplier.getItemId();
        if (supplier instanceof ComboCapacitySupplier) {
            initComboSupplier(dto, as(supplier));
        } else {
            dto.capacities = supplier.getAllValues().entrySet().stream()
                    .map(entry -> CapacityDTO.value2DTO(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList());
            dto.capacityGroups = supplier.getAllCapacityGroups().stream()
                    .map(CapacityGroup::getId)
                    .collect(Collectors.toSet());
        }
        if (supplier instanceof ExpireCapacitySupplier) {
            ExpireCapacitySupplier timeoutSupplier = (ExpireCapacitySupplier)supplier;
            long remain = timeoutSupplier.getRemainTime(System.currentTimeMillis());
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

    public static CapacitySupplierDTO supplier2RemoveDTO(CapacitySupplier supplier) {
        CapacitySupplierDTO dto = new CapacitySupplierDTO();
        dto.id = supplier.getId();
        dto.itemId = supplier.getItemId();
        dto.capacities = ImmutableList.of();
        dto.dependSuppliers = ImmutableList.of();
        dto.capacityGroups = ImmutableSet.of();
        return dto;
    }

    public static List<CapacitySupplierDTO> suppliers2RemoveDTO(Collection<? extends CapacitySupplier> suppliers) {
        return suppliers.stream()
                .map(CapacitySupplierDTO::supplier2RemoveDTO)
                .collect(Collectors.toList());
    }

    public List<CapacityDTO> getCapacities() {
        return capacities;
    }

}

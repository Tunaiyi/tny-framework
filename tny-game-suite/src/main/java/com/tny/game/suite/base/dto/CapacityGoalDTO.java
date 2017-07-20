package com.tny.game.suite.base.dto;


import com.google.common.collect.ImmutableList;
import com.tny.game.doc.annotation.DTODoc;
import com.tny.game.doc.annotation.VarDoc;
import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.protoex.annotations.ProtoExField;
import com.tny.game.suite.SuiteProtoIDs;
import com.tny.game.suite.base.capacity.CapacityGoal;
import com.tny.game.suite.base.capacity.CapacitySupplier;

import java.util.List;
import java.util.stream.Collectors;

@ProtoEx(SuiteProtoIDs.CAPACITY_GOAL_DTO)
@DTODoc(value = "能力值目标DTO")
public class CapacityGoalDTO {

    @VarDoc("能力值目标ID")
    @ProtoExField(1)
    private long id;

    @VarDoc("提供者itemID")
    @ProtoExField(2)
    private int itemID;

    @VarDoc("能力值目标依赖的能力值提供器列表")
    @ProtoExField(3)
    private List<Long> dependSuppliers;

    public static CapacityGoalDTO goal2DTO(CapacityGoal goal) {
        CapacityGoalDTO dto = new CapacityGoalDTO();
        dto.id = goal.getID();
        dto.itemID = goal.getItemID();
        dto.dependSuppliers = goal.suppliers().stream()
                .filter(CapacitySupplier::isSupplying)
                .map(CapacitySupplier::getID)
                .collect(Collectors.toList());
        return dto;
    }

    public static CapacityGoalDTO goal2RemoveDTO(CapacityGoal goal) {
        CapacityGoalDTO dto = new CapacityGoalDTO();
        dto.id = goal.getID();
        dto.itemID = goal.getItemID();
        dto.dependSuppliers = ImmutableList.of();
        return dto;
    }


}

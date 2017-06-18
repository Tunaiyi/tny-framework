package com.tny.game.suite.base.dto;

import com.tny.game.doc.annotation.DTODoc;
import com.tny.game.doc.annotation.VarDoc;
import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.protoex.annotations.ProtoExField;
import com.tny.game.suite.SuiteProtoIDs;
import com.tny.game.suite.base.capacity.CapacityGoal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@ProtoEx(SuiteProtoIDs.CAPACITY_GOAL_LIST_DTO)
@DTODoc(value = "游戏能力目标列表DTO")
public class CapacityGoalListDTO {

    @VarDoc("能力相关对象列表")
    @ProtoExField(1)
    private List<CapacityGoalDTO> goals;

    public static CapacityGoalListDTO create() {
        CapacityGoalListDTO dto = new CapacityGoalListDTO();
        dto.goals = new ArrayList<>();
        return dto;
    }

    public static CapacityGoalListDTO CapacityGoals2DTO(Collection<? extends CapacityGoal> goals) {
        CapacityGoalListDTO dto = new CapacityGoalListDTO();
        dto.goals = goals.stream()
                .map(CapacityGoalDTO::goal2DTO)
                .collect(Collectors.toList());
        return dto;
    }

    public CapacityGoalListDTO addGoal(CapacityGoal goal) {
        goals.add(CapacityGoalDTO.goal2DTO(goal));
        return this;
    }

    public CapacityGoalListDTO addDTO(CapacityGoalDTO goalDTO) {
        goals.add(goalDTO);
        return this;
    }

    public CapacityGoalListDTO addAllGoals(Collection<CapacityGoal> goals) {
        goals.forEach(this::addGoal);
        return this;
    }

    public CapacityGoalListDTO addAllDTOs(Collection<CapacityGoalDTO> goalDTOs) {
        goalDTOs.forEach(this::addDTO);
        return this;
    }


}

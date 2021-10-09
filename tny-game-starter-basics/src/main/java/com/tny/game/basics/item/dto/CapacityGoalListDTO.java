package com.tny.game.basics.item.dto;

import com.tny.game.basics.item.*;
import com.tny.game.basics.item.capacity.*;
import com.tny.game.doc.annotation.*;
import com.tny.game.protoex.annotations.*;

import java.util.*;
import java.util.stream.*;

@ProtoEx(BasicsProtoIDs.CAPACITY_GOAL_LIST_DTO)
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

	public static CapacityGoalListDTO goals2DTO(Stream<? extends CapacityGoal> goals) {
		CapacityGoalListDTO dto = new CapacityGoalListDTO();
		dto.goals = goals
				.map(CapacityGoalDTO::goal2DTO)
				.collect(Collectors.toList());
		return dto;
	}

	public static CapacityGoalListDTO goals2DTO(Collection<? extends CapacityGoal> goals) {
		return goals2DTO(goals.stream());
	}

	public static CapacityGoalListDTO goals2RemoveDTO(Stream<? extends CapacityGoal> goals) {
		CapacityGoalListDTO dto = new CapacityGoalListDTO();
		dto.goals = goals
				.map(CapacityGoalDTO::goal2RemoveDTO)
				.collect(Collectors.toList());
		return dto;
	}

	public static CapacityGoalListDTO goals2RemoveDTO(Collection<? extends CapacityGoal> goals) {
		return goals2RemoveDTO(goals.stream());
	}

	public CapacityGoalListDTO addGoal(CapacityGoal goal) {
		goals.add(CapacityGoalDTO.goal2DTO(goal));
		return this;
	}

	public CapacityGoalListDTO addDTO(CapacityGoalDTO goalDTO) {
		goals.add(goalDTO);
		return this;
	}

	public CapacityGoalListDTO addAllGoals(Collection<? extends CapacityGoal> goals) {
		goals.forEach(this::addGoal);
		return this;
	}

	public CapacityGoalListDTO addAllGoals(Stream<? extends CapacityGoal> goals) {
		goals.forEach(this::addGoal);
		return this;
	}

	public CapacityGoalListDTO addAllDTOs(Collection<CapacityGoalDTO> goalDTOs) {
		goalDTOs.forEach(this::addDTO);
		return this;
	}

	public CapacityGoalListDTO addAllDTOs(Stream<CapacityGoalDTO> goalDTOs) {
		goalDTOs.forEach(this::addDTO);
		return this;
	}

}

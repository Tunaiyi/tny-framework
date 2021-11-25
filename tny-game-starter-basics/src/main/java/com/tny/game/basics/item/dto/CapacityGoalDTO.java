package com.tny.game.basics.item.dto;

import com.google.common.collect.*;
import com.tny.game.basics.item.*;
import com.tny.game.basics.item.capacity.*;
import com.tny.game.doc.annotation.*;
import com.tny.game.protoex.annotations.*;

import java.util.*;
import java.util.stream.Collectors;

@ProtoEx(BasicsProtoIDs.CAPACITY_GOAL_DTO)
@DTODoc(value = "能力值目标DTO")
public class CapacityGoalDTO {

	@VarDoc("能力值目标ID")
	@ProtoExField(1)
	private long id;

	@VarDoc("提供者modelId")
	@ProtoExField(2)
	private int modelId;

	@VarDoc("能力值目标依赖的能力值提供器列表")
	@ProtoExField(3)
	private List<Long> dependSuppliers;

	@VarDoc("能力值组")
	@ProtoExField(4)
	private Set<Integer> capacityGroups;

	public static CapacityGoalDTO goal2DTO(CapacityGoal goal) {
		CapacityGoalDTO dto = new CapacityGoalDTO();
		dto.id = goal.getId();
		dto.modelId = goal.getModelId();
		dto.capacityGroups = goal.getSuppliersCapacityGroups()
				.stream()
				.map(CapacityGroup::getId)
				.collect(Collectors.toSet());
		dto.dependSuppliers = goal.suppliers().stream()
				.filter(CapacitySupplier::isSupplying)
				.map(CapacitySupplier::getId)
				.collect(Collectors.toList());
		return dto;
	}

	public static CapacityGoalDTO goal2RemoveDTO(CapacityGoal goal) {
		CapacityGoalDTO dto = new CapacityGoalDTO();
		dto.id = goal.getId();
		dto.modelId = goal.getModelId();
		dto.dependSuppliers = ImmutableList.of();
		dto.capacityGroups = ImmutableSet.of();
		return dto;
	}

}

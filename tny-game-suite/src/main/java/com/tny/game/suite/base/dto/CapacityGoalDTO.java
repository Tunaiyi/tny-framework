package com.tny.game.suite.base.dto;

import com.google.common.collect.*;
import com.tny.game.doc.annotation.*;
import com.tny.game.protoex.annotations.*;
import com.tny.game.suite.*;
import com.tny.game.suite.base.capacity.*;

import java.util.*;
import java.util.stream.Collectors;

@ProtoEx(SuiteProtoIDs.CAPACITY_GOAL_DTO)
@DTODoc(value = "能力值目标DTO")
public class CapacityGoalDTO {

	@VarDoc("能力值目标ID")
	@ProtoExField(1)
	private long id;

	@VarDoc("提供者itemId")
	@ProtoExField(2)
	private int itemId;

	@VarDoc("能力值目标依赖的能力值提供器列表")
	@ProtoExField(3)
	private List<Long> dependSuppliers;

	@VarDoc("能力值组")
	@ProtoExField(4)
	private Set<Integer> capacityGroups;

	public static CapacityGoalDTO goal2DTO(CapacityGoal goal) {
		CapacityGoalDTO dto = new CapacityGoalDTO();
		dto.id = goal.getId();
		dto.itemId = goal.getItemId();
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
		dto.itemId = goal.getItemId();
		dto.dependSuppliers = ImmutableList.of();
		dto.capacityGroups = ImmutableSet.of();
		return dto;
	}

}

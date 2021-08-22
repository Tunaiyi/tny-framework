package com.tny.game.suite.base.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tny.game.base.item.*;
import com.tny.game.base.item.behavior.*;
import com.tny.game.doc.annotation.*;
import com.tny.game.protoex.annotations.*;
import com.tny.game.suite.*;

import java.io.Serializable;
import java.util.*;

@ProtoEx(SuiteProtoIDs.STUFF_LIST_DTO)
@DTODoc("物品列表DTO")
public class StuffListDTO implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@VarDoc("奖励DTO")
	@ProtoExField(1)
	@JsonProperty
	private List<StuffDTO> stuffs;

	public boolean isEmpty() {
		return stuffs == null || stuffs.isEmpty();
	}

	public static StuffListDTO dealResult2DTO(DealResult dealResult) {
		StuffListDTO dto = new StuffListDTO();
		List<StuffDTO> stuffList = new ArrayList<>();
		for (DealItem<?> dealedItem : dealResult.getDealItemList()) {
			if (dealedItem.getNumber().longValue() > 0) {
				stuffList.add(StuffDTO.dealedItem2DTO(dealedItem));
			}
		}
		dto.stuffs = stuffList;
		return dto;
	}

	@SafeVarargs
	public static StuffListDTO dealItems2DTO(List<DealItem<?>>... itemLists) {
		StuffListDTO dto = new StuffListDTO();
		List<StuffDTO> stuffList = new ArrayList<>();
		for (List<DealItem<?>> list : itemLists) {
			for (DealItem<?> item : list) {
				if (item.getNumber().longValue() > 0) {
					stuffList.add(StuffDTO.dealedItem2DTO(item));
				}
			}
		}
		dto.stuffs = stuffList;
		return dto;
	}

	public static StuffListDTO awardDetail2DTO(AwardDetail costDetail) {
		if (costDetail == null) {
			return null;
		}
		return tradeItemList2DTO(costDetail.getAllTradeItemList());
	}

	public static StuffListDTO trade2DTO(TradeInfo trade) {
		if (trade == null) {
			return null;
		}
		return tradeItemList2DTO(trade.getAllTradeItem());
	}

	public static StuffListDTO awardDetails2DTO(List<AwardDetail> costDetail) {
		StuffListDTO dto = new StuffListDTO();
		List<StuffDTO> stuffList = new ArrayList<>();
		for (AwardDetail detail : costDetail) {
			for (TradeItem<ItemModel> entry : detail.getAllTradeItemList())
				stuffList.add(StuffDTO.tradeItem2DTO(entry));
		}
		dto.stuffs = stuffList;
		return dto;
	}

	public static StuffListDTO tradeItemList2DTO(Collection<TradeItem<ItemModel>> tradeItemList) {
		StuffListDTO dto = new StuffListDTO();
		List<StuffDTO> stuffList = new ArrayList<>();
		for (TradeItem<ItemModel> entry : tradeItemList) {
			if (entry.getNumber().longValue() > 0) {
				stuffList.add(StuffDTO.tradeItem2DTO(entry));
			}
		}
		dto.stuffs = stuffList;
		return dto;
	}

	public static StuffListDTO tradeInfos2DTO(List<? extends TradeInfo> tradeList) {
		StuffListDTO dto = new StuffListDTO();
		List<StuffDTO> stuffList = new ArrayList<>();
		for (TradeInfo trade : tradeList) {
			for (TradeItem<ItemModel> entry : trade.getAllTradeItem())
				stuffList.add(StuffDTO.tradeItem2DTO(entry));
		}
		dto.stuffs = stuffList;
		return dto;
	}

	public static StuffListDTO tradeInfos2DTO(TradeInfo... trades) {
		StuffListDTO dto = new StuffListDTO();
		List<StuffDTO> stuffList = new ArrayList<>();
		for (TradeInfo trade : trades) {
			if (trade == null) {
				continue;
			}
			for (TradeItem<ItemModel> entry : trade.getAllTradeItem())
				stuffList.add(StuffDTO.tradeItem2DTO(entry));
		}
		dto.stuffs = stuffList;
		return dto;
	}

	public static StuffListDTO stuffList2DTO(Trade countTradeAward) {
		if (countTradeAward == null) {
			return null;
		}
		return tradeItemList2DTO(new ArrayList<>(countTradeAward.getAllTradeItem()));
	}

	public static StuffListDTO stuffList2DTO(List<StuffDTO> stuffs) {
		StuffListDTO dto = new StuffListDTO();
		if (stuffs == null) {
			stuffs = new ArrayList<>();
		}
		dto.stuffs = stuffs;
		return dto;
	}

	public List<StuffDTO> getStuffs() {
		return stuffs;
	}

}

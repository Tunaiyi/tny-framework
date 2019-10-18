package com.tny.game.suite.base.dto;

import com.tny.game.base.item.ItemModel;
import com.tny.game.base.item.Trade;
import com.tny.game.base.item.TradeInfo;
import com.tny.game.base.item.TradeItem;
import com.tny.game.base.item.behavior.DemandResult;
import com.tny.game.base.item.behavior.TradeType;
import com.tny.game.base.item.xml.XMLDemand.TradeDemandType;
import com.tny.game.suite.base.ItemTypes;
import com.tny.game.doc.annotation.DTODoc;
import com.tny.game.doc.annotation.VarDoc;
import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.protoex.annotations.ProtoExField;
import com.tny.game.suite.SuiteProtoIDs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@ProtoEx(SuiteProtoIDs.DEMAND_RESULT_DTO)
@DTODoc(value = "判断结果DTO")
public class DemandResultDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @VarDoc("条件相关的itemID")
    @ProtoExField(1)
    protected int itemID;

    @VarDoc("条件相关的item类型")
    @ProtoExField(2)
    protected int itemType;

    @VarDoc("条件类型")
    @ProtoExField(3)
    protected int demandType;

    @VarDoc("期望值")
    @ProtoExField(5)
    protected Long expectValue;

    @VarDoc("条件相关item的ID")
    @ProtoExField(6)
    protected long id;

//    @VarDoc("期望值")
//    @ProtoExField(7)
//    protected Long currentValue;
//
//    @VarDoc("期望值")
//    @ProtoExField(8)
//    protected boolean satisfy;

    public DemandResultDTO() {
    }

    public static DemandResultDTO demandResult2DTO(DemandResult result) {
        DemandResultDTO dto = new DemandResultDTO();
        setDTO(dto, result);
        return dto;
    }

    protected static void setDTO(DemandResultDTO dto, DemandResult result) {
        dto.id = result.getId();
        dto.itemID = result.getItemId();
        dto.itemType = ItemTypes.ofItemId(result.getItemId()).getId();
        dto.demandType = result.getDemandType().getId();
//        dto.currentValue = result.getCurrentValue(Long.class);
        dto.expectValue = result.getExpectValue(Long.class);
//        dto.satisfy = result.isSatisfy();
    }

    public static List<DemandResultDTO> tradeInfo2DTOList(TradeInfo trade) {
        Collection<TradeItem<ItemModel>> tradeItemList = trade.getAllTradeItem();
        List<DemandResultDTO> list = new ArrayList<>(trade.getAllTradeItem().size());
        for (TradeItem<ItemModel> item : tradeItemList) {
            list.add(itemModel2DTO(trade.getTradeType(), item.getItemModel(), item.getNumber()));
        }
        return list;
    }

    public static List<DemandResultDTO> trade2DTOList(Trade trade) {
        Collection<TradeItem<ItemModel>> tradeItemList = trade.getAllTradeItem();
        List<DemandResultDTO> list = new ArrayList<>(trade.getAllTradeItem().size());
        for (TradeItem<ItemModel> item : tradeItemList) {
            list.add(itemModel2DTO(trade.getTradeType(), item.getItemModel(), item.getNumber()));
        }
        return list;
    }

    public static List<DemandResultDTO> trade2DTOList(TradeType tradeType, Collection<TradeItem<ItemModel>> tradeItemList) {
        List<DemandResultDTO> list = new ArrayList<>(tradeItemList.size());
        for (TradeItem<ItemModel> item : tradeItemList) {
            list.add(itemModel2DTO(tradeType, item.getItemModel(), item.getNumber()));
        }
        return list;
    }

    public static DemandResultDTO tradeItem2DTO(TradeType tradeType, TradeItem<ItemModel> tradeItem) {
        DemandResultDTO dto = new DemandResultDTO();
        dto.itemID = tradeItem.getItemModel().getId();
        dto.itemType = tradeItem.getItemModel().getItemType().getId();
        dto.expectValue = tradeItem.getNumber().longValue();
        dto.demandType = (tradeType == TradeType.COST ? TradeDemandType.COST_DEMAND_GE : TradeDemandType.RECV_DEMAND).getId();
        return dto;
    }

    public static DemandResultDTO itemModel2DTO(TradeType tradeType, ItemModel itemModel, Number number) {
        DemandResultDTO dto = new DemandResultDTO();
        dto.itemID = itemModel.getId();
        dto.itemType = itemModel.getItemType().getId();
        dto.expectValue = number.longValue();
        dto.demandType = (tradeType == TradeType.COST ? TradeDemandType.COST_DEMAND_GE : TradeDemandType.RECV_DEMAND).getId();
        return dto;
    }

    @Override
    public String toString() {
        return "DemandResultDTO [itemId=" + itemID + ", demandType="
                + demandType + ", expectValue=" + expectValue + "]";
    }

}

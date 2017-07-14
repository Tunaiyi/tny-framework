package com.tny.game.suite.cache;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.tny.game.base.item.AlterType;
import com.tny.game.base.item.Item;
import com.tny.game.base.item.ItemModel;
import com.tny.game.base.item.Trade;
import com.tny.game.base.item.TradeInfo;
import com.tny.game.base.item.TradeItem;
import com.tny.game.base.item.behavior.TradeType;
import com.tny.game.base.item.behavior.simple.SimpleTrade;
import com.tny.game.base.item.behavior.simple.SimpleTradeItem;
import com.tny.game.base.item.behavior.trade.CollectionTrade;
import com.tny.game.cache.CacheFormatter;
import com.tny.game.protobuf.PBCommon.TradeItemProto;
import com.tny.game.protobuf.PBCommon.TradeProto;
import com.tny.game.suite.base.Actions;
import com.tny.game.suite.base.GameExplorer;
import com.tny.game.suite.utils.SuiteLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public abstract class ProtoCacheFormatter<I, P extends Message> extends CacheFormatter<I, Object> {

    private static final Logger LOG = LoggerFactory.getLogger(SuiteLog.FORMATTER);

    @Autowired
    protected GameExplorer godExplorer;

    @Override
    public Object format2Save(String key, I object) {
        P proto = this.object2Proto(key, object);
        if (proto == null)
            return null;
        if (object instanceof Item) {
            Item<?> item = (Item<?>) object;
            return new ProtoItem(proto.toByteArray(), item, this.getNumber(object), this.getState(object));
        } else {
            return proto.toByteArray();
        }
    }

    @Override
    public Object format2Load(String key, Object data) {
        try {
            P proto = this.bytes2Proto((byte[]) data);
            return this.proto2Object(key, proto);
        } catch (InvalidProtocolBufferException e) {
            LOG.error("{}解析异常", this.getClass().getName(), e);
        }
        return null;
    }

    protected int getState(I object) {
        return 0;
    }

    protected <T extends Trade> T proto2Trade(TradeFactory<T> creator, TradeProto tradeProto) {
        if (tradeProto.getItemCount() == 0)
            return null;
        List<TradeItem<ItemModel>> tradeItemList = new ArrayList<>();
        for (TradeItemProto awardProto : tradeProto.getItemList()) {
            ItemModel awardModel = this.godExplorer.getModel(awardProto.getItemID());
            if (awardModel != null) {
                tradeItemList.add(new SimpleTradeItem<>(awardModel, awardProto.getNumber(), AlterType.valueOf(awardProto.getAlterType()), awardProto.getValid()));
            }
        }
        return creator.create(Actions.of(tradeProto.getAction()), TradeType.get(tradeProto.getTradeType()), tradeItemList);
    }

    protected Trade proto2Trade(TradeProto tradeProto) {
        return proto2Trade(SimpleTrade::new, tradeProto);
    }

    protected Trade proto2CollectionTrade(TradeProto tradeProto) {
        return proto2Trade(CollectionTrade::new, tradeProto);
    }

    protected TradeProto trade2Proto(TradeInfo trade) {
        if (trade == null || trade.isEmpty())
            return TradeProto.newBuilder().build();
        List<TradeItemProto> list = new ArrayList<>();
        for (TradeItem<?> item : trade.getAllTradeItem()) {
            list.add(TradeItemProto.newBuilder()
                    .setItemID(item.getItemModel().getID())
                    .setNumber(item.getNumber().longValue())
                    .setAlterType(item.getAlertType().getID())
                    .setValid(item.isValid())
                    .build());
        }
        return TradeProto.newBuilder().setAction(trade.getAction().getID())
                .addAllItem(list)
                .setAction(trade.getAction().getID())
                .setTradeType(trade.getTradeType().getID())
                .build();
    }

    protected Collection<Trade> protos2Trades(Collection<TradeProto> tradeProtos) {
        List<Trade> trades = new ArrayList<>();
        for (TradeProto tradeProto : tradeProtos) {
            Trade trade = this.proto2Trade(tradeProto);
            if (trade != null)
                trades.add(trade);
        }
        return trades;
    }

    protected Collection<TradeProto> trades2Protos(Collection<? extends TradeInfo> trades) {
        List<TradeProto> tradeProtos = new ArrayList<>();
        for (TradeInfo trade : trades) {
            tradeProtos.add(this.trade2Proto(trade));
        }
        return tradeProtos;
    }

    public abstract P bytes2Proto(byte[] data) throws InvalidProtocolBufferException;

    public abstract P object2Proto(String key, I object);

    public abstract I proto2Object(String key, P proto);

    protected Integer getNumber(I object) {
        return null;
    }

    public static <V> V toWrite(V value, V nullValue) {
        return value == null ? nullValue : value;
    }

    public static <V> V toRead(V value, V nullValue) {
        return toRead(value, nullValue, null);
    }

    public static <V> V toRead(V value, V nullValue, V defValue) {
        if (value == null || value.equals(nullValue))
            return defValue;
        return value;
    }

}

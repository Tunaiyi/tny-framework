package com.tny.game.suite.cache;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.tny.game.base.item.Item;
import com.tny.game.base.item.ItemModel;
import com.tny.game.base.item.Trade;
import com.tny.game.base.item.TradeItem;
import com.tny.game.base.item.behavior.TradeType;
import com.tny.game.base.item.behavior.simple.SimpleTrade;
import com.tny.game.base.item.behavior.simple.SimpleTradeItem;
import com.tny.game.cache.CacheFormatter;
import com.tny.game.suite.base.Actions;
import com.tny.game.suite.base.GameExplorer;
import com.tny.game.suite.proto.PBSuite261.AwardProto;
import com.tny.game.suite.proto.PBSuite261.TradeProto;
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
            return new ProtoItem(proto.toByteArray(), item, this.getNumber(object));
        } else {
            return proto.toByteArray();
        }
    }

    @Override
    public Object format4Load(String key, Object data) {
        try {
            P proto = this.bytes2Proto((byte[]) data);
            return this.proto2Object(key, proto);
        } catch (InvalidProtocolBufferException e) {
            LOG.error("{}解析异常", this.getClass().getName(), e);
        }
        return null;
    }

    protected Trade proto2Trade(TradeProto tradeProto) {
        List<TradeItem<?>> tradeItemList = new ArrayList<>();
        for (AwardProto awardProto : tradeProto.getItemList()) {
            ItemModel awardModel = this.godExplorer.getItemModel(awardProto.getItemID());
            if (awardModel != null) {
                tradeItemList.add(new SimpleTradeItem<>(awardModel, awardProto.getNumber()));
            }
        }
        return new SimpleTrade(Actions.of(tradeProto.getAction()), TradeType.get(tradeProto.getTradeType()), tradeItemList);
    }

    protected TradeProto trade2Proto(Trade trade) {
        List<AwardProto> list = new ArrayList<>();
        for (TradeItem<?> item : trade.getAllTradeItem()) {
            list.add(AwardProto.newBuilder().setItemID(item.getItemModel().getID()).setNumber(item.getNumber().longValue()).build());
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
            trades.add(this.proto2Trade(tradeProto));
        }
        return trades;
    }

    protected Collection<TradeProto> trades2Protos(Collection<Trade> trades) {
        List<TradeProto> tradeProtos = new ArrayList<>();
        for (Trade trade : trades) {
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

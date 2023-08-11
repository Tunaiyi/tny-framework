package com.tny.game.suite.cache;

import com.google.protobuf.Message;
import com.tny.game.basics.item.*;
import com.tny.game.basics.item.behavior.*;
import com.tny.game.basics.item.behavior.simple.*;
import com.tny.game.basics.item.behavior.trade.*;
import com.tny.game.cache.*;
import com.tny.game.protobuf.PBCommon.*;
import com.tny.game.suite.base.*;
import com.tny.game.suite.utils.*;
import org.slf4j.*;

import javax.annotation.Resource;
import java.util.*;

public abstract class ProtoCacheFormatter<I, P extends Message> extends CacheFormatter<I, Object> {

    private static final Logger LOG = LoggerFactory.getLogger(SuiteLog.FORMATTER);

    @Autowired
    protected GameExplorer godExplorer;

    @Override
    public Object format2Save(String key, I object) {
        P proto = this.object2Proto(key, object);
        if (proto == null) {
            return null;
        }
        try {
            if (object instanceof Item) {
                Item<?> item = (Item<?>)object;
                return new ProtoItem(proto2Bytes(proto), item, this.getNumber(object), this.getState(object));
            } else {
                return proto2Bytes(proto);
            }
        } catch (Exception e) {
            LOG.error("{}解析异常", this.getClass().getName(), e);
        }
        return null;
    }

    @Override
    public Object format2Load(String key, Object data) {
        try {
            P proto = this.bytes2Proto((byte[])data);
            return this.proto2Object(key, proto);
        } catch (Exception e) {
            LOG.error("{}解析异常", this.getClass().getName(), e);
        }
        return null;
    }

    protected int getState(I object) {
        return 0;
    }

    protected <T extends Trade> T proto2Trade(TradeFactory<T> creator, TradeProto tradeProto) {
        if (tradeProto.getItemCount() == 0) {
            return null;
        }
        List<TradeItem<ItemModel>> tradeItemList = new ArrayList<>();
        for (TradeItemProto awardProto : tradeProto.getItemList()) {
            ItemModel awardModel = this.godExplorer.getModel(awardProto.getItemId());
            if (awardModel != null) {
                tradeItemList.add(new SimpleTradeItem<>(awardModel, awardProto.getNumber(), AlterType.valueOf(awardProto.getAlterType()),
                        awardProto.getValid()));
            }
        }
        return creator.create(Actions.check(tradeProto.getAction()), TradeType.get(tradeProto.getTradeType()), tradeItemList);
    }

    protected Trade proto2Trade(TradeProto tradeProto) {
        return proto2Trade(SimpleTrade::new, tradeProto);
    }

    protected Trade proto2CollectionTrade(TradeProto tradeProto) {
        return proto2Trade(CollectionTrade::new, tradeProto);
    }

    protected TradeProto trade2Proto(TradeInfo trade) {
        if (trade == null || trade.isEmpty()) {
            return TradeProto.newBuilder().build();
        }
        List<TradeItemProto> list = new ArrayList<>();
        for (TradeItem<?> item : trade.getAllTradeItem()) {
            list.add(TradeItemProto.newBuilder()
                    .setItemId(item.getItemModel().getId())
                    .setNumber(item.getNumber().longValue())
                    .setAlterType(item.getAlertType().getId())
                    .setValid(item.isValid())
                    .build());
        }
        return TradeProto.newBuilder().setAction(trade.getAction().getId())
                .addAllItem(list)
                .setAction(trade.getAction().getId())
                .setTradeType(trade.getTradeType().getId())
                .build();
    }

    protected Collection<Trade> protos2Trades(Collection<TradeProto> tradeProtos) {
        List<Trade> trades = new ArrayList<>();
        for (TradeProto tradeProto : tradeProtos) {
            Trade trade = this.proto2Trade(tradeProto);
            if (trade != null) {
                trades.add(trade);
            }
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

    public abstract P bytes2Proto(byte[] data) throws Exception;

    public byte[] proto2Bytes(P proto) throws Exception {
        return proto.toByteArray();
    }

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
        if (value == null || value.equals(nullValue)) {
            return defValue;
        }
        return value;
    }

}

/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.item.behavior.simple;

import com.tny.game.basics.item.*;
import com.tny.game.basics.item.behavior.*;

import java.util.*;

public class Trades {

    public <T> DemandParamEntry<T> demandParam(DemandParam param, T value) {
        return new SimpleDemandParamEntry<>(param, value);
    }

    private static Map<DemandParam, Object> demandParamMap(DemandParamEntry<?>... params) {
        if (params == null) {
            return Collections.emptyMap();
        }
        Map<DemandParam, Object> paramMap = new HashMap<>();
        for (DemandParamEntry<?> entry : params) {
            paramMap.put(entry.getParam(), entry.getValue());
        }
        return paramMap;
    }

    public static Trade trade(Action action, TradeType type, long id, StuffModel itemModel, Number number, AlterType alertType,
            DemandParamEntry<?>... params) {
        return new SimpleTrade(action, type, new SimpleTradeItem<>(id, itemModel, number, alertType, true, demandParamMap(params)));
    }

    public static Trade trade(Action action, TradeType type, long id, StuffModel itemModel, Number number, AlterType alertType,
            Map<DemandParam, Object> paramMap) {
        return new SimpleTrade(action, type, new SimpleTradeItem<>(id, itemModel, number, alertType, true, paramMap));
    }

    public static Trade trade(Action action, TradeType type, StuffModel itemModel, Number number, AlterType alertType,
            DemandParamEntry<?>... params) {
        return new SimpleTrade(action, type, new SimpleTradeItem<>(0, itemModel, number, alertType, true, demandParamMap(params)));
    }

    public static Trade trade(Action action, TradeType type, StuffModel itemModel, Number number, AlterType alertType,
            Map<DemandParam, Object> paramMap) {
        return new SimpleTrade(action, type, new SimpleTradeItem<>(0, itemModel, number, alertType, true, paramMap));
    }

    public static Trade trade(Action action, TradeType type, TradeItem<?> item, Number number) {
        return new SimpleTrade(action, type, new SimpleTradeItem<>(item, number));
    }

    public static Trade trade(Action action, TradeType type, TradeItem<?>... items) {
        return new SimpleTrade(action, type, items);
    }

    public static Trade trade(Action action, TradeType type, Collection<TradeItem<?>> items) {
        return new SimpleTrade(action, type, items);
    }

    public static Trade award(Action action, long id, StuffModel itemModel, Number number, AlterType alertType,
            DemandParamEntry<?>... params) {
        return trade(action, TradeType.AWARD, id, itemModel, number, alertType, params);
    }

    public static Trade award(Action action, long id, StuffModel itemModel, Number number, AlterType alertType,
            Map<DemandParam, Object> paramMap) {
        return trade(action, TradeType.AWARD, id, itemModel, number, alertType, paramMap);
    }

    public static Trade award(Action action, StuffModel itemModel, Number number, AlterType alertType,
            DemandParamEntry<?>... params) {
        return trade(action, TradeType.AWARD, new SimpleTradeItem<>(0, itemModel, number, alertType, true, demandParamMap(params)));
    }

    public static Trade award(Action action, StuffModel itemModel, Number number, AlterType alertType,
            Map<DemandParam, Object> paramMap) {
        return trade(action, TradeType.AWARD, new SimpleTradeItem<>(0, itemModel, number, alertType, true, paramMap));
    }

    public static Trade award(Action action, TradeItem<?> item, Number number) {
        return trade(action, TradeType.AWARD, new SimpleTradeItem<>(item, number));
    }

    public static Trade award(Action action, TradeItem<?>... items) {
        return trade(action, TradeType.AWARD, items);
    }

    public static Trade award(Action action, Collection<TradeItem<?>> items) {
        return trade(action, TradeType.AWARD, items);
    }

    public static Trade cost(Action action, long id, StuffModel itemModel, Number number, AlterType alertType,
            DemandParamEntry<?>... params) {
        return trade(action, TradeType.DEDUCT, id, itemModel, number, alertType, params);
    }

    public static Trade cost(Action action, long id, StuffModel itemModel, Number number, AlterType alertType,
            Map<DemandParam, Object> paramMap) {
        return trade(action, TradeType.DEDUCT, id, itemModel, number, alertType, paramMap);
    }

    public static Trade cost(Action action, StuffModel itemModel, Number number, AlterType alertType,
            DemandParamEntry<?>... params) {
        return trade(action, TradeType.DEDUCT, new SimpleTradeItem<>(0, itemModel, number, alertType, true, demandParamMap(params)));
    }

    public static Trade cost(Action action, StuffModel itemModel, Number number, AlterType alertType,
            Map<DemandParam, Object> paramMap) {
        return trade(action, TradeType.DEDUCT, new SimpleTradeItem<>(0, itemModel, number, alertType, true, paramMap));
    }

    public static Trade cost(Action action, TradeItem<?> item, Number number) {
        return trade(action, TradeType.DEDUCT, new SimpleTradeItem<>(item, number));
    }

    public static Trade cost(Action action, TradeItem<?>... items) {
        return trade(action, TradeType.DEDUCT, items);
    }

    public static Trade cost(Action action, Collection<TradeItem<?>> items) {
        return trade(action, TradeType.DEDUCT, items);
    }

    public static <I extends StuffModel> TradeItem<I> item(long id, I itemModel, Number number, AlterType alertType,
            DemandParamEntry<?>... params) {
        return new SimpleTradeItem<>(id, itemModel, number, alertType, true, demandParamMap(params));
    }

    public static <I extends StuffModel> TradeItem<I> item(long id, I itemModel, Number number, AlterType alertType,
            Map<DemandParam, Object> paramMap) {
        return new SimpleTradeItem<>(id, itemModel, number, alertType, true, paramMap);
    }

    public static <I extends StuffModel> TradeItem<I> item(I itemModel, Number number, AlterType alertType,
            DemandParamEntry<?>... params) {
        return new SimpleTradeItem<>(0, itemModel, number, alertType, true, demandParamMap(params));
    }

    public static <I extends StuffModel> TradeItem<I> item(I itemModel, Number number, AlterType alertType,
            Map<DemandParam, Object> paramMap) {
        return new SimpleTradeItem<>(0, itemModel, number, alertType, true, paramMap);
    }

    public static <I extends StuffModel> TradeItem<I> item(TradeItem<I> item, Number number) {
        return new SimpleTradeItem<>(item, number);
    }

}
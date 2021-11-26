package com.tny.game.basics.item.behavior.simple;

import com.tny.game.basics.item.*;
import com.tny.game.basics.item.behavior.*;

import java.util.*;

public class SimpleTradeItem<I extends StuffModel> implements TradeItem<I> {

	private long id;

	private AlterType alertType;

	private I itemModel;

	private Number number;

	private boolean valid;

	private Map<DemandParam, Object> paramMap = new HashMap<>();

	public SimpleTradeItem(DemandResult result, AlterType alertType, DemandParamEntry<?>... entries) {
		this(result, alertType, true, entries);
	}

	@SuppressWarnings("unchecked")
	public SimpleTradeItem(DemandResult result, AlterType alertType, boolean valid, DemandParamEntry<?>... entries) {
		this.id = result.getId();
		this.alertType = alertType;
		this.itemModel = (I)result.getItemModel();
		this.number = result.getExpectValue(Number.class);
		this.valid = valid;
		for (DemandParamEntry<?> entry : entries) {
			this.paramMap.put(entry.getParam(), entry.getValue());
		}
	}

	public SimpleTradeItem(DemandResult result, AlterType alertType, Map<DemandParam, Object> paramMap) {
		this(result, alertType, true, paramMap);
	}

	@SuppressWarnings("unchecked")
	public SimpleTradeItem(DemandResult result, AlterType alertType, boolean valid, Map<DemandParam, Object> paramMap) {
		this.id = result.getId();
		this.alertType = alertType;
		this.itemModel = (I)result.getItemModel();
		this.number = result.getExpectValue(Number.class);
		this.valid = valid;
		if (paramMap != null) {
			this.paramMap.putAll(paramMap);
		}
	}

	public SimpleTradeItem(long id, I itemModel, Number number, AlterType alertType, Map<DemandParam, Object> paramMap) {
		this(id, itemModel, number, alertType, true, paramMap);
	}

	public SimpleTradeItem(I itemModel, Number number, AlterType alertType, boolean valid, Map<DemandParam, Object> paramMap) {
		this(0, itemModel, number, alertType, valid, paramMap);
	}

	public SimpleTradeItem(long id, I itemModel, Number number, AlterType alertType, boolean valid, Map<DemandParam, Object> paramMap) {
		super();
		this.id = id;
		this.itemModel = itemModel;
		this.number = number;
		this.alertType = alertType;
		this.valid = valid;
		if (paramMap != null) {
			this.paramMap.putAll(paramMap);
		}
	}

	public SimpleTradeItem(TradeItem<I> item, Number number) {
		this(item, number, true);
	}

	public SimpleTradeItem(TradeItem<I> item, Number number, boolean valid) {
		super();
		this.id = item.getId();
		this.itemModel = item.getItemModel();
		this.number = number;
		this.alertType = item.getAlertType();
		this.valid = valid;
		if (paramMap != null) {
			this.paramMap.putAll(item.getParamMap());
		}
	}

	public SimpleTradeItem(I model, Number number, AlterType alertType) {
		this(0, model, number, alertType, true);
	}

	public SimpleTradeItem(long id, I model, Number number, AlterType alertType) {
		this(id, model, number, alertType, true);
	}

	public SimpleTradeItem(I model, Number number, AlterType alertType, boolean valid) {
		this(0, model, number, alertType, valid);
	}

	public SimpleTradeItem(long id, I model, Number number, AlterType alertType, boolean valid) {
		super();
		this.id = id;
		this.itemModel = model;
		this.number = number;
		this.alertType = alertType;
		this.valid = valid;
	}

	public SimpleTradeItem(I model, Number number) {
		this(model, number, true);
	}

	public SimpleTradeItem(I model, Number number, boolean valid) {
		this(0, model, number, AlterType.CHECK, valid);
	}

	@Override
	public AlterType getAlertType() {
		return alertType;
	}

	@Override
	public boolean isValid() {
		return valid;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <SI extends I> SI getItemModel() {
		return (SI)itemModel;
	}

	@Override
	public long getId() {
		return 0;
	}

	@Override
	public Number getNumber() {
		return number;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <P> P getParam(DemandParam param) {
		Object value = this.paramMap.get(param);
		if (value == null) {
			return null;
		}
		return (P)value;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <P> P getParam(DemandParam param, P defaultValue) {
		Object value = this.getParam(param);
		return value == null ? defaultValue : (P)value;
	}

	@Override
	public Map<DemandParam, Object> getParamMap() {
		return Collections.unmodifiableMap(paramMap);
	}

	@Override
	public String toString() {
		return "SimpleTradeItem [alertType=" + alertType + ", itemModel=" + itemModel + ", number=" + number + "]";
	}

}

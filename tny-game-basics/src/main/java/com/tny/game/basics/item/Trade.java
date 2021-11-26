package com.tny.game.basics.item;

import com.tny.game.basics.item.behavior.simple.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.tny.game.common.number.NumberAide.*;

/**
 * 交易对象 获取扣除和奖励的物品信息
 *
 * @author KGTny
 */
public interface Trade extends TradeInfo {

	/**
	 * 合并重复的ItemModel, 数量累加
	 *
	 * @return 返回合并后的新的Trade对象
	 */
	default Trade merge() {
		Map<StuffModel, Number> itemNumMap = new HashMap<>();
		for (TradeItem<StuffModel> item : this.getAllTradeItem()) {
			Number value = itemNumMap.get(item.getItemModel());
			if (value == null) {
				value = 0L;
			}
			value = add(value, item.getNumber());
			itemNumMap.put(item.getItemModel(), value);
		}
		List<TradeItem<StuffModel>> tradeItemList = itemNumMap.entrySet().stream()
				.map(entry -> new SimpleTradeItem<>(entry.getKey(), entry.getValue()))
				.collect(Collectors.toList());
		return new SimpleTrade(this.getAction(), this.getTradeType(), tradeItemList);
	}

	/**
	 * 改变Trade中item的数量, 如果返回null或0则移除
	 *
	 * @return 返回改变后的新的Trade对象
	 */
	default Trade alter(Function<TradeItem<StuffModel>, Number> fun) {
		return new SimpleTrade(this.getAction(), this.getTradeType(),
				this.getAllTradeItem().stream()
						.map(i -> {
							Number number = fun.apply(i);
							if (number != null && number.doubleValue() > 0) {
								return new SimpleTradeItem<>(i.getId(), i.getItemModel(), number, i.getAlertType(), i.getParamMap());
							}
							return null;
						})
						.filter(Objects::nonNull)
						.collect(Collectors.toList()));
	}

}

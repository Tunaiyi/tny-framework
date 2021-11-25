package com.tny.game.basics.item.behavior;

import com.tny.game.basics.item.*;
import com.tny.game.basics.item.behavior.simple.*;

import java.util.Map;

import static com.tny.game.basics.item.ItemsImportKey.*;

/**
 * 奖励对象
 *
 * @author KGTny
 */
public abstract class AbstractAward extends DemandParamsObject implements Award {

	public abstract void init();

	@Override
	public TradeItem<StuffModel> createTradeItem(boolean valid, StuffModel awardModel, Map<String, Object> attributeMap) {
		AlterType type = this.getAlterType();
		Map<DemandParam, Object> paramMap = this.countAndSetDemandParams($PARAMS, attributeMap);
		Number number = this.countNumber(awardModel, attributeMap);
		if (number.doubleValue() > 0.0) {
			return new SimpleTradeItem<>(awardModel, number, type == null ? AlterType.IGNORE : type, valid, paramMap);
		} else {
			return null;
		}
	}

}

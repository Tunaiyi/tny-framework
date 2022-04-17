package com.tny.game.basics.item.behavior.plan;

import com.tny.game.basics.item.*;
import com.tny.game.basics.item.behavior.*;
import com.tny.game.basics.item.xml.*;
import com.tny.game.basics.log.*;
import com.tny.game.expr.*;
import org.slf4j.*;

import java.util.Map;

public class SimpleAward extends BaseAward {

	private static final Logger LOGGER = LoggerFactory.getLogger(LogName.WAREHOUSE);

	/**
	 * 改变方式
	 */
	protected AlterType alterType;

	/**
	 * 获得概率
	 */
	protected ExprHolder probability;

	/**
	 * 奖励物品id
	 */
	protected String itemAlias;

	/**
	 * 涉及Item
	 */
	protected ExprHolder itemAliasFx;

	/**
	 * 奖励数量公式
	 */
	protected ExprHolder fx;

	@Override
	public String getItemAlias(Map<String, Object> attributeMap) {
		return this.itemAliasFx != null ? this.itemAliasFx.createExpr().putAll(attributeMap).execute(String.class)
				: this.itemAlias;
	}

	@Override
	public Number countNumber(ItemModel model, Map<String, Object> attributes) {
		return this.fx.createExpr()
				.putAll(attributes)
				.put(AbstractItemModel.ACTION_AWARD_MODEL_NAME, model)
				.execute(Number.class);
	}

	@Override
	public AlterType getAlterType() {
		return this.alterType;
	}

	@Override
	public void init() {
		if (this.itemAlias == null) {
			if (this.itemAliasFx == null) {
				AliasCollectUtils.addAlias(null);
			}
		} else {
			AliasCollectUtils.addAlias(this.itemAlias);
		}
	}

	@Override
	public int getProbability(Map<String, Object> attributeMap) {
		return this.probability.createExpr()
				.putAll(attributeMap)
				.execute(Integer.class);
	}

}

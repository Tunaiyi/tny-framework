package com.tny.game.basics.item.behavior;

import com.tny.game.basics.exception.*;
import com.tny.game.basics.item.*;
import com.tny.game.basics.item.model.*;
import com.tny.game.basics.item.xml.*;
import com.tny.game.expr.*;

import java.util.Map;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * 事物执行行为操作的条件
 *
 * @author KGTny
 */
public abstract class BaseDemand extends DemandParamsObject implements Demand, ItemsImportKey {

	/**
	 * 条件所涉及的item ID
	 */
	protected String itemAlias;

	/**
	 * 涉及Item
	 */
	protected ExprHolder itemAliasFx;

	/**
	 * 条件涉及的item 别名，用于表达式
	 */
	protected String name;

	/**
	 * 类型
	 */
	protected DemandType demandType;

	/**
	 * 当前值表达式
	 */
	protected ExprHolder current;

	/**
	 * 希望值表达式
	 */
	protected ExprHolder expect;

	/**
	 * 条件判断表达式
	 */
	protected ExprHolder fx;

	/**
	 * 改变类型
	 */
	protected AlterType alertType;

	public AlterType getAlertType() {
		return this.alertType;
	}

	@Override
	public String getItemAlias(Map<String, Object> attributeMap) {
		return this.itemAliasFx != null ? this.itemAliasFx.createExpr().putAll(attributeMap).execute(String.class) : this.itemAlias;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public DemandType getDemandType() {
		return this.demandType;
	}

	@Override
	public Object countExpectValue(long playerId, Map<String, Object> attributeMap) {
		String alias = this.getItemAlias(attributeMap);
		if (alias == null) {
			return null;
		}
		this.setAttrMap(playerId, alias, attributeMap);
		this.countAndSetDemandParams($PARAMS, attributeMap);
		return this.expect.createExpr().putAll(attributeMap).execute(Object.class);
	}

	@Override
	public Object countCurrentValue(long playerId, Map<String, Object> attributeMap) {
		String alias = this.getItemAlias(attributeMap);
		if (alias == null) {
			return null;
		}
		ItemModel demandModel = this.setAttrMap(playerId, alias, attributeMap);
		this.countAndSetDemandParams($PARAMS, attributeMap);
		return demandModel.currentFormula().putAll(attributeMap).execute(Object.class);
	}

	@Override
	public DemandResult checkDemandResult(long playerId, Map<String, Object> attributeMap) {
		String alias = this.getItemAlias(attributeMap);
		if (alias == null) {
			return null;
		}
		ItemModel demandModel = this.getItemModel(alias);
		this.setAttrMap(playerId, alias, attributeMap);
		Map<DemandParam, Object> paramMap = this.countAndSetDemandParams($PARAMS, attributeMap);
		Expr currentFormula = getCurrentFormula(demandModel);
		Object current = currentFormula != null ? currentFormula.putAll(attributeMap).execute(Object.class) : null;
		Object expect = this.expect != null ? this.expect.createExpr().putAll(attributeMap).execute(Object.class) : null;
		boolean satisfy = this.checkSatisfy(current, expect, demandModel, attributeMap);
		if (this.getDemandType() == TradeDemandType.COST_DEMAND_GE) {
			Number costId = (Number)attributeMap.getOrDefault(ItemModel.ATTRIBUTE_KEY_COST_ITEM_ID, 0L);
			return new CostDemandResult(costId.longValue(), as(demandModel), this.demandType, current, expect, satisfy, this.alertType, paramMap);
		} else {
			return new DemandResult(0L, demandModel, this.demandType, current, expect, satisfy, paramMap);
		}
	}

	private Expr getCurrentFormula(ItemModel demandModel) {
		if (this.current != null) {
			return this.current.createExpr();
		} else {
			if (this.demandType.isCost()) {
				return demandModel.currentFormula();
			}
		}
		return null;
	}

	private Expr getDemandFormula(ItemModel demandModel) {
		if (this.fx != null) {
			return this.fx.createExpr();
		} else {
			if (this.demandType.isCost()) {
				return demandModel.demandFormula();
			}
		}
		return null;
	}

	@Override
	public boolean isSatisfy(long playerId, Map<String, Object> attributeMap) {
		return this.checkDemandResult(playerId, attributeMap).isSatisfy();
	}

	protected boolean checkSatisfy(Object current, Object expect, ItemModel demandModel, Map<String, Object> attribute) {
		Expr fxFormula = getDemandFormula(demandModel);
		return fxFormula.putAll(attribute).put(CURRENT_VALUE, current).put(EXPECT_VALUE, expect).execute(Boolean.class);
	}

	private ItemModel getItemModel(String alias) {
		ModelExplorer itemModelExplorer = this.context.getItemModelExplorer();
		ItemModel model = itemModelExplorer.getModelByAlias(alias);
		if (model == null) {
			throw new GameRuningException(ItemResultCode.MODEL_NO_EXIST, alias);
		}
		return model;
	}

	private ItemModel setAttrMap(long playerId, String alias, Map<String, Object> attributeMap) {
		ItemModel model = this.getItemModel(alias);
		Item<?> item;
		attributeMap.put(DEMAND_MODEL, model);
		ItemExplorer itemExplorer = this.context.getItemExplorer();
		if (itemExplorer.hasItemManager(model.getItemType())) {
			item = itemExplorer.getItem(playerId, model.getId());
			if (this.name != null) {
				attributeMap.put(this.name, item);
			}
			attributeMap.put(alias, item);
			attributeMap.put(DEMAND_ITEM, item);
		}
		attributeMap.putIfAbsent(DEMAND_ITEM, null);
		return model;
	}

	public void init(ItemModel itemModel, ItemModelContext context) {
		this.init(context);
		if (this.alertType == null) {
			this.alertType = AlterType.CHECK;
		}
		if (this.itemAlias == null) {
			this.itemAlias = itemModel.getAlias();
		}
		if (this.demandType == null) {
			this.demandType = TradeDemandType.COST_DEMAND_GE;
		}
		if (this.itemAlias == null) {
			if (this.itemAliasFx == null) {
				AliasCollectUtils.addAlias(null);
			}
		} else {
			AliasCollectUtils.addAlias(this.itemAlias);
		}
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [demandType=" + this.demandType + ", expect=" + this.expect + "]";
	}

}

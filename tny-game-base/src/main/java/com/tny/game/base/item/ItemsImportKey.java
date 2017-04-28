package com.tny.game.base.item;

/**
 * Item导入key
 * Created by Kun Yang on 16/3/16.
 */
public interface ItemsImportKey {

    /**
     * 事物对象在有自己相关的行为和操作中所代表的引用名字
     */
    String ACTION_ITEM_NAME = "self";
    /**
     * 事物对象模型在有自己相关的行为和操作中所代表的引用名字
     */
    String ACTION_ITEM_MODEL_NAME = "model";
    /**
     * 在计算奖励公式中奖励事务的模型引用名字
     */
    String ACTION_AWARD_MODEL_NAME = "awModel";
    /**
     * 当前条件相关item对象引用名字
     */
    String DEMAND_ITEM = "demandItem";
    /**
     * 当前条件相关item对象引用名字
     */
    String DEMAND_MODEL = "demandModel";

    /**
     * 当前值在公式中所表示的名字
     */
    String CURRENT_VALUE = "current";
    /**
     * 期望值在公式中所表示的名字
     */
    String EXPECT_VALUE = "expect";
    /**
     * 计算是否符合消耗物品的条件
     */
    String FX_FORMULA = CURRENT_VALUE + " >= " + EXPECT_VALUE;
    /**
     * 内容
     */
    String DEMAIND_PARAMS = "params";

}

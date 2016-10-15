package com.tny.game.base.item;

/**
 * 事物模型总管理器
 *
 * @author KGTny
 */
public interface ModelExplorer {

    <IM extends Model> IM getModel(int itemID);

    <IM extends Model> IM getModelByAlias(String itemAlias);

}

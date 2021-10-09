package com.tny.game.basics.item;

/**
 * 事物模型总管理器
 *
 * @author KGTny
 */
public interface ModelExplorer {

    <IM extends Model> IM getModel(int itemID);

    <IM extends Model> IM getModelByAlias(String itemAlias);

    <M extends ModelManager<? extends Model>> M getModelManager(ItemType itemType);
}

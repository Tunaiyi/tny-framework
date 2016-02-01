package com.tny.game.base.item;

/**
 * 事物模型总管理器
 *
 * @author KGTny
 */
public interface ItemModelExplorer {

    public <IM extends ItemModel> IM getItemModel(int itemID);

    public <IM extends ItemModel> IM getItemModelByAlias(String itemAlias);

}

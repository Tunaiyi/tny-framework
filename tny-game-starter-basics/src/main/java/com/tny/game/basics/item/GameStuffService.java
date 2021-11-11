package com.tny.game.basics.item;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/20 1:11 上午
 */
public interface GameStuffService<IM extends ItemModel, SM extends ItemModel, S extends Stuff<?>, O extends GameStuffOwner<IM, SM, S>> {

	ItemType getOwnerItemType();

	O findOwner(long playerId);

}

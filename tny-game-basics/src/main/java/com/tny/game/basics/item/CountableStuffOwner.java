package com.tny.game.basics.item;

/**
 * @author KGTny
 * @ClassName: CountableStuffStorage
 * @Description: 物品项拥有者
 * @date 2011-11-3 上午9:50:52
 * <p>
 * <p>
 * <br>
 */
public interface CountableStuffOwner<IM extends ItemModel, SM extends CountableStuffModel, S extends Stuff<SM>>
		extends StuffOwner<IM, S> {

	/**
	 * 检测是否满了
	 *
	 * @param model  测试物品模型
	 * @param number 添加数量
	 * @return 溢出返回 true 否则返回 false
	 */
	boolean isOverflow(SM model, AlterType type, Number number);

	/**
	 * 检测是否足够
	 *
	 * @param model  测试物品模型
	 * @param number 扣除数量
	 * @return 不足返回 true 否则返回 false
	 */
	boolean isNotEnough(SM model, AlterType type, Number number);

}
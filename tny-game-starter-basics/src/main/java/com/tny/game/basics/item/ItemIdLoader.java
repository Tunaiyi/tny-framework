package com.tny.game.basics.item;

import java.util.List;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/18 5:11 下午
 */
public interface ItemIdLoader<I> {

	List<AnyId> findIdList(Class<I> itemClass, long player);

	List<AnyId> findAllIdList(Class<I> itemClass);

}

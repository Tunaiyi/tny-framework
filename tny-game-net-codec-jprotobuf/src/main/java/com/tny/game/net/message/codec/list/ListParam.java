package com.tny.game.net.message.codec.list;

import java.util.List;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/12 11:48 上午
 */
public interface ListParam<T, C> {

    List<T> getValueList();

    C getValueArray();

}

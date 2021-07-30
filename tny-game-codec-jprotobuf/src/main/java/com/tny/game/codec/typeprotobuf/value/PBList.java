package com.tny.game.codec.typeprotobuf.value;

import java.util.List;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/12 11:48 上午
 */
public interface PBList<T, C> {

    List<T> getValueList();

    C getValueArray();

}

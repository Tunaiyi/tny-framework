package com.tny.game.protoex;

import com.tny.game.common.enums.EnumID;

/**
 * protoEx枚举接口
 *
 * @author KGTny
 */
public interface ProtoExEnum extends EnumID<Integer> {

    /**
     * 枚举ID
     *
     * @return
     */
    @Override
    Integer getID();

    /**
     * 枚举名字
     *
     * @return
     */
    String name();

}
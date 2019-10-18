package com.tny.game.protoex;

import com.tny.game.common.enums.EnumIdentifiable;

/**
 * protoEx枚举接口
 *
 * @author KGTny
 */
public interface ProtoExEnum extends EnumIdentifiable<Integer> {

    /**
     * 枚举ID
     *
     * @return
     */
    @Override
    Integer getId();

    /**
     * 枚举名字
     *
     * @return
     */
    String name();

}
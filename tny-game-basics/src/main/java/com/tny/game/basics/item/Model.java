package com.tny.game.basics.item;

import com.tny.game.common.tag.*;

public interface Model extends Taggable {

    /**
     * 事物ID
     *
     * @return
     */
    int getId();

    /**
     * 获取别名
     *
     * @return
     */
    String getAlias();

    /**
     * 获取描述
     *
     * @return
     */
    String getDesc();

}

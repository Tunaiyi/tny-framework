package com.tny.game.base.item;

import com.tny.game.common.tag.Taggable;

public interface Model extends Taggable {

    /**
     * 事物ID
     *
     * @return
     */
    int getID();

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

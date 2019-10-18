package com.tny.game.base.item;

import com.tny.game.common.tag.*;

import java.util.Set;

public interface Any<M extends Model> extends Identifier, Taggable {

    /**
     * 获取对象ID
     *
     * @return
     */
    long getId();

    /**
     * 获取对象别名
     *
     * @return
     */
    String getAlias();

    /**
     * 获取该事物对象ID
     *
     * @return
     */
    int getItemId();

    /**
     * 获取该事物对象的模型
     *
     * @return
     */
    M getModel();

    @Override
    default Set<Object> tags() {
        return this.getModel().tags();
    }
}

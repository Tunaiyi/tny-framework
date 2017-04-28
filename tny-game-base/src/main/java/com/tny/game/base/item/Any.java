package com.tny.game.base.item;

import com.tny.game.common.tag.Taggable;

import java.util.Set;

public interface Any<M extends Model> extends Identifiable, Taggable {

    /**
     * 获取对象ID
     *
     * @return
     */
    long getID();

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
    int getItemID();

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

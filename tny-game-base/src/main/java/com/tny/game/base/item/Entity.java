package com.tny.game.base.item;

import com.tny.game.common.tag.*;

import java.util.Set;

public interface Entity<M extends Model> extends Owned, Taggable {

    /**
     * @return 获取对象ID
     */
    long getId();

    /**
     * @return 玩家ID
     */
    long getPlayerId();

    @Override
    default long getOwnerId() {
        return this.getPlayerId();
    }

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

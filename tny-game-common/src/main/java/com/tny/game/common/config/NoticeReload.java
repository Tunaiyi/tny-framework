package com.tny.game.common.config;

public interface NoticeReload {

    /**
     * 添加可重读接口 <br>
     *
     * @param reloadable 添加的可重读接口
     */
    public void addReloadable(Reloadable reloadable);

    /**
     * 移除可重读接口 <br>
     *
     * @param reloadable 移除的可重读接口
     */
    public void removeReloadable(Reloadable reloadable);

    /**
     * 清楚所有可重读接口<br>
     */
    public void clearReloadable();
}

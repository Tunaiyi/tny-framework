package com.tny.game.net.session;

/**
 * Session 输入消息处理器
 * Created by Kun Yang on 2017/2/23.
 */
public interface SessionInputEventHandler<UID, S extends NetSession<UID>> {

    /**
     * 处理指定session的输入
     *
     * @param session 处理指定session
     */
    void onInput(S session);

}

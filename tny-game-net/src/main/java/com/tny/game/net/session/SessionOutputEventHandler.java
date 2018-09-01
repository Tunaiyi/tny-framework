package com.tny.game.net.session;

/**
 * Session 输出消息处理器
 * Created by Kun Yang on 2017/2/23.
 */
public interface SessionOutputEventHandler<UID, S extends NetSession<UID>> {

    /**
     * 处理指定session的输
     *
     * @param session 处理指定session
     */
    void onOutput(S session);

}

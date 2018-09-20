package com.tny.game.net.transport;


import com.tny.game.net.transport.listener.SessionHolderListener;

import java.util.*;
import java.util.stream.Stream;

/**
 * @author KGTny
 * @ClassName: SessionHolder
 * @Description: 会话管理器接口
 * @date 2011-10-11 下午6:50:17
 * <p>
 * 会话管理器接口
 * <p>
 * 负责管理会话<br>
 */
public interface SessionKeeper<UID> {

    /**
     * @return 获取用户类型
     */
    String getUserType();

    /**
     * <p>
     * <p>
     * 获取指定userId对应的Session <br>
     *
     * @param userId 指定的Key
     * @return 返回获取的session, 无session返回null
     */
    Session<UID> getSession(UID userId);

    /**
     * 获取所有的sessions
     *
     * @return 返回sessions map
     */
    Map<UID, Session<UID>> getAllSessions();

    /**
     * <p>
     * <p>
     * 获取指定userId对应的Session <br>
     *
     * @param userId 指定的Key
     * @return 返回获取的session, 无session返回null
     */
    boolean isOnline(UID userId);

    /**
     * 发信息给用户 <br>
     *
     * @param userId  用户ID
     * @param content 消息内容
     */
    void send2User(UID userId, MessageContext<UID> content);

    /**
     * 发信息给用户集合 <br>
     *
     * @param userIds 用户ID列表
     * @param content    消息内容
     */
    void send2Users(Collection<UID> userIds, MessageContext<UID> content);

    /**
     * 发信息给用户集合 <br>
     *
     * @param userIdsStream 用户ID流
     * @param content       消息内容
     */
    void send2Users(Stream<UID> userIdsStream, MessageContext<UID> content);

    /**
     * 发送给所有在线的用户 <br>
     */
    void send2AllOnline(MessageContext<UID> content);

    /**
     * 使指定userId的session下线
     *
     * @param userId 指定userId
     * @return 返回下线session
     */
    Session<UID> offline(UID userId);

    /**
     * 使指定userId的session关闭
     *
     * @param userId 指定userId
     * @return 返回下线session
     */
    Session<UID> close(UID userId);

    /**
     * 使所有session下线
     */
    void offlineAll();

    /**
     * 是所有session关闭
     */
    void closeAll();

    /**
     * @return 所有session数量
     */
    int size();

    /**
     * 计算在线人数
     */
    int countOnline();

    /**
     * 添加监听器
     *
     * @param listener 监听器
     */
    void addListener(SessionHolderListener<UID> listener);

    /**
     * 添加监听器列表
     *
     * @param listeners 监听器列表
     */
    void addListener(Collection<SessionHolderListener<UID>> listeners);

    /**
     * 移除监听器
     *
     * @param listener 监听器
     */
    void removeListener(SessionHolderListener<UID> listener);

}

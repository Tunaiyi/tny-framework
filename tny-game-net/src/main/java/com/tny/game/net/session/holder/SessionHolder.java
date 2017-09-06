package com.tny.game.net.session.holder;


import com.tny.game.net.message.MessageContent;
import com.tny.game.net.session.Session;
import com.tny.game.net.session.holder.listener.SessionHolderListener;

import java.util.Collection;
import java.util.Map;
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
public interface SessionHolder {

    /**
     * <p>
     * <p>
     * 获取指定uid对应的Session <br>
     *
     * @param uid 指定的Key
     * @return 返回获取的session, 无session返回null
     */
    <U> Session<U> getSession(String userGroup, U uid);

    /**
     * <p>
     * <p>
     * 获取指定uid对应的Session <br>
     *
     * @param uid 指定的Key
     * @return 返回获取的session, 无session返回null
     */
    boolean isOnline(String userGroup, Object uid);

    /**
     * 发信息给用户 <br>
     *
     * @param userGroup 用户组
     * @param uid       用户ID
     * @param content   消息内容
     * @return 是否加入发送队列
     */
    boolean send2User(String userGroup, Object uid, MessageContent content);

    /**
     * 发信息给用户集合 <br>
     *
     * @param userGroup 用户组
     * @param uidColl   用户ID列表
     * @param content   消息内容
     * @return 返回发送数量
     */
    void send2Users(String userGroup, Collection<?> uidColl, MessageContent<?> content);

    /**
     * 发信息给用户集合 <br>
     *
     * @param userGroup  用户组
     * @param uidsStream 用户ID流
     * @param content    消息内容
     * @return 返回发送数量
     */
    void send2Users(String userGroup, Stream<?> uidsStream, MessageContent<?> content);

    /**
     * 发送给所有在线的用户 <br>
     *
     * @return 返回发送的人数
     */
    void send2AllOnline(String userGroup, MessageContent<?> content);

    /**
     * 计算在线人数
     */
    int countOnline(String userGroup);

    /**
     * 使指定group和uid的session下线, 不立即失效
     *
     * @param userGroup 指定group
     * @param uid       指定uid
     * @return 返回下线session
     */
    default <U> Session<U> offline(String userGroup, U uid) {
        return this.offline(userGroup, uid, false);
    }

    /**
     * 使指定group和uid的session下线, 并指定是否立即失效
     *
     * @param userGroup 指定group
     * @param uid       指定uid
     * @param invalid   true为立即失效,false为只是下线
     * @return 返回下线session
     */
    <U> Session<U> offline(String userGroup, U uid, boolean invalid);

    /**
     * 使指定用户组的所有session下线, session不立即失效
     *
     * @param userGroup 指定用户组
     */
    default void offlineAll(String userGroup) {
        offlineAll(userGroup, false);
    }

    /**
     * 使指定用户组的所有session下线, 并指定是否立即失效
     *
     * @param userGroup 指定用户组
     * @param invalid   true为立即失效,false为只是下线
     */
    void offlineAll(String userGroup, boolean invalid);

    /**
     * 是所有session下线, session不立即失效
     */
    default void offlineAll() {
        offlineAll(false);
    }
    /**
     * 使所有session下线, 并指定是否立即失效
     *
     * @param invalid true为立即失效,false为只是下线
     */
    void offlineAll(boolean invalid);

    /**
     * @return 所有session数量
     */
    int size();

    /**
     * 通过group获取sessions
     *
     * @param userGroup 指定group
     * @return 返回sessions map
     */
    <U> Map<U, Session<U>> getSessionsByGroup(String userGroup);

    /**
     * 添加监听器
     *
     * @param listener 监听器
     */
    void addListener(SessionHolderListener listener);

    /**
     * 添加监听器列表
     *
     * @param listeners 监听器列表
     */
    void addListener(Collection<SessionHolderListener> listeners);

    /**
     * 移除监听器
     *
     * @param listener 监听器
     */
    void removeListener(SessionHolderListener listener);

}

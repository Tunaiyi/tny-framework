package com.tny.game.net.dispatcher;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.base.Protocol;
import com.tny.game.net.base.listener.SessionListener;

import java.util.Collection;
import java.util.Map;

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
     * 获取指定key对应的Session <br>
     *
     * @param key 指定的Key
     * @return 返回获取的session, 无session返回null
     */
    Session getSession(String userGroup, Object key);

    /**
     * <p>
     * <p>
     * 获取指定key对应的Session <br>
     *
     * @param key 指定的Key
     * @return 返回获取的session, 无session返回null
     */
    boolean isOnline(String userGroup, Object key);

    /**
     * 创建会话组,无则创建,无则不操作 <br>
     *
     * @return 成功返回true
     */
    boolean createChannel(String userGroup, Object channelID);

    /**
     * 删除会话标识对应会话 <br>
     *
     * @return 成功返回true 失败返回false
     */
    boolean removeChannel(String userGroup, Object channelID);

    /**
     * 是否存在会话组 <br>
     *
     * @return 存在返回true 失败返回false
     */
    boolean isExistChannel(String userGroup, Object channelID);

    /**
     * 添加用户到指定的会话组 <br>
     *
     * @param uid 用户ID
     * @return 添加成功返回ture 失败返回false
     */
    boolean addChannelUser(String userGroup, Object channelID, Object uid);

    /**
     * 获取会话组人数 <br>
     *
     * @return 返回会话组人数
     */
    int getChannelSize(String userGroup, Object channelID);

    /**
     * 添加用户集合到指定的会话组 <br>
     *
     * @return 添加成功的数量
     */
    int addChannelUser(String userGroup, Object channelID, Collection<?> uidColl);

    /**
     * 用户是否在指定的会话组 <br>
     *
     * @return 是否存在, 存在返回true 不存在返回false
     */
    boolean isInChannel(String userGroup, Object channelID, Object uid);

    /**
     * 移除指定会话组的用户 <br>
     *
     * @param uid 用户ID
     * @return 是否移除成功, 成功返回true, 失败返回false
     */
    boolean removeChannelUser(String userGroup, Object channelID, Object uid);

    /**
     * 移除指定会话组的用户集合 <br>
     *
     * @return 返回成功删除的数量
     */
    int removeChannelUser(String userGroup, Object channelID, Collection<?> uidColl);

    /**
     * 移除指定会话的所有用户 <br>
     */
    void clearChannelUser(String userGroup, Object channelID);

    /**
     * 发信息给用户 <br>
     *
     * @param uid 用户ID
     * @return 返回是否成功
     */
    boolean send2User(String userGroup, Object uid, Protocol protocol, ResultCode code, Object body);

    /**
     * 发信息给Session对应的用户 <br>
     *
     * @param session  会话
     * @param protocol 协议
     * @param code     结果码
     * @param body     消息体
     * @return 返回是否发送成功
     */
    boolean send2User(Session session, Protocol protocol, ResultCode code, Object body);

    /**
     * 发信息给用户 <br>
     *
     * @param uid 用户ID
     * @return 返回是否成功
     */
    boolean send2Channel(String userGroup, Object uid, Protocol protocol, ResultCode code, Object body);

    /**
     * 发信息给用户集合 <br>
     *
     * @return 返回成功的数量
     */
    int send2User(String userGroup, Collection<?> uidColl, Protocol protocol, ResultCode code, Object body);

    /**
     * 发送给所有在线的用户 <br>
     *
     * @return 返回发送的人数
     */
    int send2AllOnline(String userGroup, Protocol protocol, ResultCode code, Object body);

    /**
     * 计算在线人数
     */
    int countOnline(String userGroup);

    /**
     * <p>
     * <p>
     * 删除指定key和session对应的Session <br>
     *
     * @param session 指定的session
     */
    void offline(Session session);

    /**
     * T下线
     *
     * @param key
     * @return
     */
    Session offline(String userGroup, Object key);

    /**
     * 全部T下线
     */
    void offlineAll(String userGroup);

    /**
     * 全部T下线
     */
    void offlineAll();

    int size();

    Map<Object, Session> getSessionMapByGroup(String userGroup);

    void addSessionListener(SessionListener listener);

    void addSessionListener(Collection<SessionListener> listeners);

    void removeSessionListener(SessionListener listener);

    void clearSessionListener();

}

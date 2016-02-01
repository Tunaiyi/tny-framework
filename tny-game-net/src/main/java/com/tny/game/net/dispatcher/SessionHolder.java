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
    public abstract Session getSession(String userGroup, Object key);

    /**
     * <p>
     * <p>
     * 获取指定key对应的Session <br>
     *
     * @param key 指定的Key
     * @return 返回获取的session, 无session返回null
     */
    public abstract boolean isOnline(String userGroup, Object key);

    /**
     * 创建会话组,无则创建,无则不操作 <br>
     *
     * @param object 会话标识
     * @return 成功返回true
     */
    public abstract boolean createChannel(String userGroup, Object channelID);

    /**
     * 删除会话标识对应会话 <br>
     *
     * @param object 会话标识
     * @return 成功返回true 失败返回false
     */
    public abstract boolean removeChannel(String userGroup, Object channelID);

    /**
     * 是否存在会话组 <br>
     *
     * @param object 标识的会话
     * @return 存在返回true 失败返回false
     */
    public abstract boolean isExsitChannel(String userGroup, Object channelID);

    /**
     * 添加用户到指定的会话组 <br>
     *
     * @param object 指定的会话组
     * @param uid    用户ID
     * @return 添加成功返回ture 失败返回false
     */
    public abstract boolean addChannelUser(String userGroup, Object channelID, Object uid);

    /**
     * 获取会话组人数 <br>
     *
     * @param groupID 会话组
     * @return 返回会话组人数
     */
    public abstract int getChannelSize(String userGroup, Object channelID);

    /**
     * 添加用户集合到指定的会话组 <br>
     *
     * @param object 指定的会话组
     * @param uid    用户ID集合
     * @return 添加成功的数量
     */
    public abstract int addChannelUser(String userGroup, Object channelID, Collection<?> uidColl);

    /**
     * 用户是否在指定的会话组 <br>
     *
     * @return 是否存在, 存在返回true 不存在返回false
     */
    public abstract boolean isInChannel(String userGroup, Object channelID, Object uid);

    /**
     * 移除指定会话组的用户 <br>
     *
     * @param object 指定的会话标识
     * @param uid    用户ID
     * @return 是否移除成功, 成功返回true, 失败返回false
     */
    public abstract boolean removeChannelUser(String userGroup, Object channelID, Object uid);

    /**
     * 移除指定会话组的用户集合 <br>
     *
     * @param object 指定的会话标识
     * @param uid    用户集合ID
     * @return 返回成功删除的数量
     */
    public abstract int removeChannelUser(String userGroup, Object channelID, Collection<?> uidColl);

    /**
     * 移除指定会话的所有用户 <br>
     *
     * @param object 指定会话的标识
     */
    public abstract void clearChannelUser(String userGroup, Object channelID);

    /**
     * 发信息给用户 <br>
     *
     * @param uid 用户ID
     * @return 返回是否成功
     */
    public abstract boolean send2User(String userGroup, Object uid, Protocol protocol, ResultCode code, Object body);

    /**
     * 发信息给用户 <br>
     *
     * @param uid 用户ID
     * @return 返回是否成功
     */
    public abstract boolean send2Channel(String userGroup, Object uid, Protocol protocol, ResultCode code, Object body);

    /**
     * 发信息给用户集合 <br>
     *
     * @param uid 用户ID集合
     * @return 返回成功的数量
     */
    public abstract int send2User(String userGroup, Collection<?> uidColl, Protocol protocol, ResultCode code, Object body);

    /**
     * 发送给所有在线的用户 <br>
     *
     * @param response 消息
     * @return 返回发送的人数
     */
    public abstract int send2AllOnline(String userGroup, Protocol protocol, ResultCode code, Object body);

    /**
     * 计算在线人数
     */
    public abstract int countOnline(String userGroup);

    /**
     * <p>
     * <p>
     * 删除指定key和session对应的Session <br>
     *
     * @param session 指定的session
     */
    public abstract void offline(Session session);

    /**
     * T下线
     *
     * @param key
     * @return
     */
    public abstract Session offline(String userGroup, Object key);

    /**
     * 全部T下线
     */
    public abstract void offlineAll(String userGroup);

    /**
     * 全部T下线
     */
    public abstract void offlineAll();

    public abstract int size();

    public Map<Object, Session> getSessionMapByGroup(String userGroup);

    public void addSessionListener(SessionListener listener);

    public void addSessionListener(Collection<SessionListener> listeners);

    public void removeSessionListener(SessionListener listener);

    public void clearSessionListener();

}

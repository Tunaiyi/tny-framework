package com.tny.game.suite.net;

import com.tny.game.base.item.Identifiable;
import com.tny.game.common.result.ResultCode;
import com.tny.game.net.base.ProtocolUtils;
import com.tny.game.net.dispatcher.CommandResult;
import com.tny.game.net.dispatcher.Session;
import com.tny.game.net.dispatcher.SessionHolder;
import com.tny.game.suite.login.GroupType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.tny.game.suite.SuiteProfiles.*;

@Component
@Profile({SERVER, GAME_KAFKA, SERVER_KAFKA, GAME})
public class SessionService {

    @Autowired
    private SessionHolder sessionHolder;

    /**
     * <p>
     * <p>
     * 获取指定key对应的Session <br>
     *
     * @param key 指定的Key
     * @return 返回获取的session, 无session返回null
     */
    public Session getSession(Object key) {
        return this.sessionHolder.getSession(Session.DEFAULT_USER_GROUP, key);
    }

    public void sendResponse2User(Identifiable gamer, CommandResult message) {
        this.sessionHolder.send2User(
                Session.DEFAULT_USER_GROUP,
                gamer.getPlayerID(),
                ProtocolUtils.PUSH,
                message.getResultCode(),
                message.getBody());
    }

    public void sendResponse2User(Identifiable gamer, Object message) {
        this.sessionHolder.send2User(
                Session.DEFAULT_USER_GROUP,
                gamer.getPlayerID(),
                ProtocolUtils.PUSH,
                ResultCode.SUCCESS,
                message);
    }

    public void sendResponse2User(Identifiable gamer, ResultCode resultCode) {
        this.sessionHolder.send2User(
                Session.DEFAULT_USER_GROUP,
                gamer.getPlayerID(),
                ProtocolUtils.PUSH,
                resultCode,
                null);
    }

    public void sendResponse2User(Identifiable gamer, ResultCode resultCode, Object message) {
        this.sessionHolder.send2User(
                Session.DEFAULT_USER_GROUP,
                gamer.getPlayerID(),
                ProtocolUtils.PUSH,
                resultCode,
                message);
    }

    public void sendResponse2User(Collection<? extends Identifiable> gamerList, Object message, Long... filterPlayerIDs) {
        if (gamerList.isEmpty())
            return;
        List<Long> userIDSet = new ArrayList<Long>(gamerList.size());
        List<Long> filter = Collections.emptyList();
        if (filterPlayerIDs.length > 0)
            filter = Arrays.asList(filterPlayerIDs);
        for (Identifiable gamer : gamerList) {
            if (!filter.contains(gamer.getPlayerID()))
                userIDSet.add(gamer.getPlayerID());
        }
        this.sessionHolder.send2User(
                Session.DEFAULT_USER_GROUP,
                userIDSet,
                ProtocolUtils.PUSH,
                ResultCode.SUCCESS,
                message);
    }

    public void sendResponse2UserID(Collection<Long> gamerList, Object message) {
        if (gamerList.isEmpty())
            return;
        this.sessionHolder.send2User(
                Session.DEFAULT_USER_GROUP,
                gamerList,
                ProtocolUtils.PUSH,
                ResultCode.SUCCESS,
                message);
    }

    public void sendResponse2Group(GroupType groupType, Identifiable identifiable, Object message) {
        this.sessionHolder.send2Channel(
                Session.DEFAULT_USER_GROUP,
                groupType.createGroupID(identifiable),
                ProtocolUtils.PUSH,
                ResultCode.SUCCESS,
                message);
    }

    public void sendResponse2Online(Object message) {
        this.sessionHolder.send2AllOnline(
                Session.DEFAULT_USER_GROUP,
                ProtocolUtils.PUSH,
                ResultCode.SUCCESS,
                message);
    }

    /**
     * 创建会话组,无则创建,无则不操作 <br>
     *
     * @param groupID 会话标识
     * @return 成功返回true
     */
    public boolean createGroup(Object groupID) {
        return this.sessionHolder.createChannel(Session.DEFAULT_USER_GROUP, groupID);
    }

    /**
     * 删除会话标识对应会话 <br>
     *
     * @param groupID 会话标识
     * @return 成功返回true 失败返回false
     */
    public boolean removeGroup(Object groupID) {
        return this.sessionHolder.removeChannel(Session.DEFAULT_USER_GROUP, groupID);
    }

    /**
     * 是否存在会话组 <br>
     *
     * @param groupID 标识的会话
     * @return 存在返回true 失败返回false
     */
    public boolean isExsitGroup(Object groupID) {
        return this.sessionHolder.isExistChannel(Session.DEFAULT_USER_GROUP, groupID);
    }

    /**
     * 添加用户集合到指定的会话组 <br>
     *
     * @param groupID 指定的会话组
     * @param uidColl 用户ID集合
     */
    public void addGroup(Object groupID, Collection<?> uidColl) {
        this.sessionHolder.addChannelUser(Session.DEFAULT_USER_GROUP, groupID, uidColl);
    }

    /**
     * 添加用户到指定的会话组 <br>
     *
     * @param groupID 指定的会话组
     * @param uid     用户ID
     */
    public void addGroup(Object groupID, Object uid) {
        this.sessionHolder.addChannelUser(Session.DEFAULT_USER_GROUP, groupID, uid);
    }

    /**
     * 移除指定会话组的用户 <br>
     *
     * @param groupID 指定的会话标识
     * @param uid     用户ID
     * @return 是否移除成功, 成功返回true, 失败返回false
     */
    public boolean removeGroupUser(Object groupID, Object uid) {
        return this.sessionHolder.removeChannelUser(Session.DEFAULT_USER_GROUP, groupID, uid);
    }

    /**
     * 移除指定会话组的用户集合 <br>
     *
     * @param groupID 指定的会话标识
     * @param uidColl 用户集合ID
     * @return 返回成功删除的数量
     */
    public int removeGroupUser(Object groupID, Collection<?> uidColl) {
        return this.sessionHolder.removeChannelUser(Session.DEFAULT_USER_GROUP, groupID, uidColl);
    }

    /**
     * 移除指定会话的所有用户 <br>
     *
     * @param groupID 指定会话的标识
     */
    public void clearGroupUser(Object groupID) {
        this.sessionHolder.clearChannelUser(Session.DEFAULT_USER_GROUP, groupID);
    }

    /**
     * 指定用户 playerID 是否在线
     *
     * @param playerID 玩家ID
     * @return 返回ture 表示在线, 否则表示是下线
     */
    public boolean isOnline(long playerID) {
        return this.sessionHolder.isOnline(Session.DEFAULT_USER_GROUP, playerID);
    }

    /**
     * @return 默认用户组session数量
     */
    public int size() {
        return this.sessionHolder.getSessionMapByGroup(Session.DEFAULT_USER_GROUP).size();
    }

    /**
     * @return 获取所有默认组用户session
     */
    public Set<Session> getAllUserSession() {
        return new HashSet<>(this.sessionHolder.getSessionMapByGroup(Session.DEFAULT_USER_GROUP).values());
    }

    /**
     * 将所有session下线
     */
    public void offlineAll() {
        this.sessionHolder.offlineAll(Session.DEFAULT_USER_GROUP);
    }

    /**
     * 下线指定playerID的session
     *
     * @param playerID 指定玩家ID
     */
    public void offline(long playerID) {
        this.sessionHolder.offline(Session.DEFAULT_USER_GROUP, playerID);
    }

}

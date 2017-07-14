package com.tny.game.suite.net;

import com.tny.game.base.item.Identifiable;
import com.tny.game.common.result.ResultCode;
import com.tny.game.net.base.AppConstants;
import com.tny.game.net.base.CoreResponseCode;
import com.tny.game.net.command.CommandResult;
import com.tny.game.net.message.MessageContent;
import com.tny.game.net.message.ProtocolAide;
import com.tny.game.net.session.Session;
import com.tny.game.net.session.holder.SessionHolder;
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
        return this.sessionHolder.getSession(AppConstants.DEFAULT_USER_GROUP, key);
    }

    public void sendResponse2User(Identifiable gamer, CommandResult message) {
        this.sessionHolder.send2User(
                AppConstants.DEFAULT_USER_GROUP,
                gamer.getPlayerID(),
                MessageContent.toPush(
                        ProtocolAide.PUSH,
                        message.getResultCode(),
                        message.getBody()
                ));
    }

    public void sendResponse2User(Identifiable gamer, Object body) {
        this.sessionHolder.send2User(
                AppConstants.DEFAULT_USER_GROUP,
                gamer.getPlayerID(),
                MessageContent.toPush(
                        ProtocolAide.PUSH,
                        CoreResponseCode.SUCCESS,
                        body
                ));
    }

    public void sendResponse2User(Identifiable gamer, ResultCode resultCode) {
        this.sessionHolder.send2User(
                AppConstants.DEFAULT_USER_GROUP,
                gamer.getPlayerID(),
                MessageContent.toPush(
                        ProtocolAide.PUSH,
                        resultCode
                ));
    }

    public void sendResponse2User(Identifiable gamer, ResultCode resultCode, Object body) {
        this.sessionHolder.send2User(
                AppConstants.DEFAULT_USER_GROUP,
                gamer.getPlayerID(),
                MessageContent.toPush(
                        ProtocolAide.PUSH,
                        resultCode,
                        body
                ));
    }

    public void sendResponse2User(Collection<? extends Identifiable> gamerList, Object body, Long... filterPlayerIDs) {
        if (gamerList.isEmpty())
            return;
        List<Long> userIDSet = new ArrayList<>(gamerList.size());
        List<Long> filter = Collections.emptyList();
        if (filterPlayerIDs.length > 0)
            filter = Arrays.asList(filterPlayerIDs);
        for (Identifiable gamer : gamerList) {
            if (!filter.contains(gamer.getPlayerID()))
                userIDSet.add(gamer.getPlayerID());
        }
        this.sessionHolder.send2Users(
                AppConstants.DEFAULT_USER_GROUP,
                userIDSet,
                MessageContent.toPush(
                        ProtocolAide.PUSH,
                        ResultCode.SUCCESS,
                        body
                ));
    }

    public void sendResponse2UserID(Collection<Long> gamerList, Object body) {
        if (gamerList.isEmpty())
            return;
        this.sessionHolder.send2Users(
                AppConstants.DEFAULT_USER_GROUP,
                gamerList,
                MessageContent.toPush(
                        ProtocolAide.PUSH,
                        ResultCode.SUCCESS,
                        body
                ));
    }

    public void sendResponse2Online(Object body) {
        this.sessionHolder.send2AllOnline(
                AppConstants.DEFAULT_USER_GROUP,
                MessageContent.toPush(
                        ProtocolAide.PUSH,
                        ResultCode.SUCCESS,
                        body
                ));
    }

    /**
     * 指定用户 playerID 是否在线
     *
     * @param playerID 玩家ID
     * @return 返回ture 表示在线, 否则表示是下线
     */
    public boolean isOnline(long playerID) {
        return this.sessionHolder.isOnline(AppConstants.DEFAULT_USER_GROUP, playerID);
    }

    /**
     * @return 默认用户组session数量
     */
    public int size() {
        return this.sessionHolder.getSessionsByGroup(AppConstants.DEFAULT_USER_GROUP).size();
    }

    /**
     * @return 获取所有默认组用户session
     */
    public Set<Session> getAllUserSession() {
        return new HashSet<>(this.sessionHolder.getSessionsByGroup(AppConstants.DEFAULT_USER_GROUP).values());
    }

    /**
     * 将所有session下线
     */
    public void offlineAll() {
        this.sessionHolder.offlineAll(AppConstants.DEFAULT_USER_GROUP);
    }

    /**
     * 下线指定playerID的session
     *
     * @param playerID 指定玩家ID
     */
    public void offline(long playerID) {
        this.sessionHolder.offline(AppConstants.DEFAULT_USER_GROUP, playerID);
    }
    // /**
    //  * 创建会话组,无则创建,无则不操作 <br>
    //  *
    //  * @param groupID 会话标识
    //  * @return 成功返回true
    //  */
    // public boolean createGroup(Object groupID) {
    //     return this.sessionHolder.createChannel(AppConstants.DEFAULT_USER_GROUP, groupID);
    // }
    //
    // /**
    //  * 删除会话标识对应会话 <br>
    //  *
    //  * @param groupID 会话标识
    //  * @return 成功返回true 失败返回false
    //  */
    // public boolean removeGroup(Object groupID) {
    //     return this.sessionHolder.removeChannel(AppConstants.DEFAULT_USER_GROUP, groupID);
    // }
    //
    // /**
    //  * 是否存在会话组 <br>
    //  *
    //  * @param groupID 标识的会话
    //  * @return 存在返回true 失败返回false
    //  */
    // public boolean isExsitGroup(Object groupID) {
    //     return this.sessionHolder.isExistChannel(AppConstants.DEFAULT_USER_GROUP, groupID);
    // }
    //
    // /**
    //  * 添加用户集合到指定的会话组 <br>
    //  *
    //  * @param groupID 指定的会话组
    //  * @param uidColl 用户ID集合
    //  */
    // public void addGroup(Object groupID, Collection<?> uidColl) {
    //     this.sessionHolder.addChannelUser(AppConstants.DEFAULT_USER_GROUP, groupID, uidColl);
    // }
    //
    // /**
    //  * 添加用户到指定的会话组 <br>
    //  *
    //  * @param groupID 指定的会话组
    //  * @param uid     用户ID
    //  */
    // public void addGroup(Object groupID, Object uid) {
    //     this.sessionHolder.addChannelUser(AppConstants.DEFAULT_USER_GROUP, groupID, uid);
    // }
    //
    // /**
    //  * 移除指定会话组的用户 <br>
    //  *
    //  * @param groupID 指定的会话标识
    //  * @param uid     用户ID
    //  * @return 是否移除成功, 成功返回true, 失败返回false
    //  */
    // public boolean removeGroupUser(Object groupID, Object uid) {
    //     return this.sessionHolder.removeChannelUser(AppConstants.DEFAULT_USER_GROUP, groupID, uid);
    // }
    //
    // /**
    //  * 移除指定会话组的用户集合 <br>
    //  *
    //  * @param groupID 指定的会话标识
    //  * @param uidColl 用户集合ID
    //  * @return 返回成功删除的数量
    //  */
    // public int removeGroupUser(Object groupID, Collection<?> uidColl) {
    //     return this.sessionHolder.removeChannelUser(AppConstants.DEFAULT_USER_GROUP, groupID, uidColl);
    // }
    //
    // /**
    //  * 移除指定会话的所有用户 <br>
    //  *
    //  * @param groupID 指定会话的标识
    //  */
    // public void clearGroupUser(Object groupID) {
    //     this.sessionHolder.clearChannelUser(AppConstants.DEFAULT_USER_GROUP, groupID);
    // }

}

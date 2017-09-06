package com.tny.game.suite.net;

import com.tny.game.base.item.Identifiable;
import com.tny.game.common.result.ResultCode;
import com.tny.game.net.base.CoreResponseCode;
import com.tny.game.net.command.CommandResult;
import com.tny.game.net.message.Protocol;
import com.tny.game.net.message.ProtocolAide;
import com.tny.game.net.session.Session;
import com.tny.game.net.session.holder.SessionHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static com.tny.game.net.base.AppConstants.*;
import static com.tny.game.net.message.MessageContent.*;
import static com.tny.game.suite.SuiteProfiles.*;

@Component
@Profile({SERVER, GAME_KAFKA, SERVER_KAFKA, GAME})
public class SessionService {

    @Autowired
    private SessionHolder sessionHolder;

    /**
     * 获取默认用户组中与指定uid对应的Session <br>
     *
     * @param uid 用户ID
     * @return 返回获取的session, 无session返回null
     */
    public <UID> Session<UID> getSession(UID uid) {
        return this.sessionHolder.getSession(DEFAULT_USER_GROUP, uid);
    }


    /**
     * 获取group用户组中与指定uid对应的Session <br>
     *
     * @param group 用户组
     * @param uid   用户ID
     * @return 返回获取的session, 无session返回null
     */
    public <UID> Session<UID> getSession(String group, UID uid) {
        return this.sessionHolder.getSession(DEFAULT_USER_GROUP, uid);
    }


    /**
     * 推送消息给用户
     *
     * @param group      用户组
     * @param uid        用户ID
     * @param protocol   协议
     * @param resultCode 结果码
     * @param body       消息体
     */
    public void push2User(String group, long uid, Protocol protocol, ResultCode resultCode, Object body) {
        this.sessionHolder.send2User(group, uid, toPush(protocol, resultCode, body));
    }


    /**
     * 推送消息给用户
     *
     * @param group    用户组
     * @param uid      用户ID
     * @param protocol 协议
     * @param body     消息体
     */
    public void push2User(String group, long uid, Protocol protocol, Object body) {
        this.push2User(group, uid, protocol, CoreResponseCode.SUCCESS, body);
    }


    /**
     * 推送消息给用户
     *
     * @param group    用户组
     * @param uid      用户ID
     * @param protocol 协议
     */
    public void push2User(String group, long uid, Protocol protocol, CommandResult message) {
        this.push2User(group, uid, protocol, message.getResultCode(), message.getBody());
    }

    /**
     * 推送消息给用户
     *
     * @param group      用户组
     * @param uid        用户ID
     * @param resultCode 结果码
     * @param body       消息体
     */
    public void push2User(String group, long uid, ResultCode resultCode, Object body) {
        this.push2User(group, uid, ProtocolAide.PUSH, resultCode, body);
    }

    /**
     * 推送消息给用户
     *
     * @param group 用户组
     * @param uid   用户ID
     * @param body  消息体
     */
    public void push2User(String group, long uid, Object body) {
        this.push2User(group, uid, ProtocolAide.PUSH, CoreResponseCode.SUCCESS, body);
    }

    /**
     * 推送消息给用户
     *
     * @param group 用户组
     * @param uid   用户ID
     */
    public void push2User(String group, long uid, CommandResult message) {
        this.push2User(group, uid, ProtocolAide.PUSH, message.getResultCode(), message.getBody());
    }

    /**
     * 推送消息给用户
     *
     * @param uid        用户ID
     * @param protocol   协议
     * @param resultCode 结果码
     * @param body       消息体
     */
    public void push2User(long uid, Protocol protocol, ResultCode resultCode, Object body) {
        this.sessionHolder.send2User(DEFAULT_USER_GROUP, uid, toPush(protocol, resultCode, body));
    }


    /**
     * 推送消息给用户
     *
     * @param uid      用户ID
     * @param protocol 协议
     * @param body     消息体
     */
    public void push2User(long uid, Protocol protocol, Object body) {
        this.push2User(DEFAULT_USER_GROUP, uid, protocol, CoreResponseCode.SUCCESS, body);
    }

    /**
     * 推送消息给用户
     *
     * @param uid      用户ID
     * @param protocol 协议
     */
    public void push2User(long uid, Protocol protocol, CommandResult message) {
        this.push2User(DEFAULT_USER_GROUP, uid, protocol, message.getResultCode(), message.getBody());
    }

    /**
     * 推送消息给用户
     *
     * @param uid        用户ID
     * @param resultCode 结果码
     * @param body       消息体
     */
    public void push2User(long uid, ResultCode resultCode, Object body) {
        this.push2User(DEFAULT_USER_GROUP, uid, ProtocolAide.PUSH, resultCode, body);
    }

    /**
     * 推送消息给用户
     *
     * @param uid  用户ID
     * @param body 消息体
     */
    public void push2User(long uid, Object body) {
        this.push2User(DEFAULT_USER_GROUP, uid, ProtocolAide.PUSH, CoreResponseCode.SUCCESS, body);
    }


    /**
     * 推送消息给用户
     *
     * @param uid 用户ID
     */
    public void push2User(long uid, CommandResult message) {
        this.push2User(DEFAULT_USER_GROUP, uid, ProtocolAide.PUSH, message.getResultCode(), message.getBody());
    }

    /**
     * 推送消息给用户
     *
     * @param group      用户组
     * @param user       可标识用户的对象
     * @param protocol   协议
     * @param resultCode 结果码
     * @param body       消息体
     */
    public void push2User(String group, Identifiable user, Protocol protocol, ResultCode resultCode, Object body) {
        this.sessionHolder.send2User(group, user, toPush(protocol, resultCode, body));
    }


    /**
     * 推送消息给用户
     *
     * @param group    用户组
     * @param user     可标识用户的对象
     * @param protocol 协议
     * @param body     消息体
     */
    public void push2User(String group, Identifiable user, Protocol protocol, Object body) {
        this.push2User(group, user, protocol, CoreResponseCode.SUCCESS, body);
    }


    /**
     * 推送消息给用户
     *
     * @param group    用户组
     * @param user     可标识用户的对象
     * @param protocol 协议
     */
    public void push2User(String group, Identifiable user, Protocol protocol, CommandResult message) {
        this.push2User(group, user, protocol, message.getResultCode(), message.getBody());
    }

    /**
     * 推送消息给用户
     *
     * @param group      用户组
     * @param user       可标识用户的对象
     * @param resultCode 结果码
     * @param body       消息体
     */
    public void push2User(String group, Identifiable user, ResultCode resultCode, Object body) {
        this.push2User(group, user, ProtocolAide.PUSH, resultCode, body);
    }

    /**
     * 推送消息给用户
     *
     * @param group 用户组
     * @param user  可标识用户的对象
     * @param body  消息体
     */
    public void push2User(String group, Identifiable user, Object body) {
        this.push2User(group, user, ProtocolAide.PUSH, CoreResponseCode.SUCCESS, body);
    }

    /**
     * 推送消息给用户
     *
     * @param group 用户组
     * @param user  可标识用户的对象
     */
    public void push2User(String group, Identifiable user, CommandResult message) {
        this.push2User(group, user, ProtocolAide.PUSH, message.getResultCode(), message.getBody());
    }

    /**
     * 推送消息给用户
     *
     * @param user       可标识用户的对象
     * @param protocol   协议
     * @param resultCode 结果码
     * @param body       消息体
     */
    public void push2User(Identifiable user, Protocol protocol, ResultCode resultCode, Object body) {
        this.sessionHolder.send2User(DEFAULT_USER_GROUP, user, toPush(protocol, resultCode, body));
    }


    /**
     * 推送消息给用户
     *
     * @param user     可标识用户的对象
     * @param protocol 协议
     * @param body     消息体
     */
    public void push2User(Identifiable user, Protocol protocol, Object body) {
        this.push2User(DEFAULT_USER_GROUP, user, protocol, CoreResponseCode.SUCCESS, body);
    }

    /**
     * 推送消息给用户
     *
     * @param user     可标识用户的对象
     * @param protocol 协议
     */
    public void push2User(Identifiable user, Protocol protocol, CommandResult message) {
        this.push2User(DEFAULT_USER_GROUP, user, protocol, message.getResultCode(), message.getBody());
    }

    /**
     * 推送消息给用户
     *
     * @param user       可标识用户的对象
     * @param resultCode 结果码
     * @param body       消息体
     */
    public void push2User(Identifiable user, ResultCode resultCode, Object body) {
        this.push2User(DEFAULT_USER_GROUP, user, ProtocolAide.PUSH, resultCode, body);
    }

    /**
     * 推送消息给用户
     *
     * @param user 可标识用户的对象
     * @param body 消息体
     */
    public void push2User(Identifiable user, Object body) {
        this.push2User(DEFAULT_USER_GROUP, user, ProtocolAide.PUSH, CoreResponseCode.SUCCESS, body);
    }


    /**
     * 推送消息给用户
     *
     * @param user 可标识用户的对象
     */
    public void push2User(Identifiable user, CommandResult message) {
        this.push2User(DEFAULT_USER_GROUP, user, ProtocolAide.PUSH, message.getResultCode(), message.getBody());
    }

    /**
     * 推送消息给用户流
     *
     * @param group      用户组
     * @param users      用户流
     * @param protocol   协议
     * @param resultCode 结果码
     * @param body       消息体
     */
    public void push2Users(String group, Stream<? extends Identifiable> users, Protocol protocol, ResultCode resultCode, Object body) {
        this.sessionHolder.send2User(group, users, toPush(protocol, resultCode, body));
    }


    /**
     * 推送消息给用户流
     *
     * @param group    用户组
     * @param users    用户流
     * @param protocol 协议
     * @param body     消息体
     */
    public void push2Users(String group, Stream<? extends Identifiable> users, Protocol protocol, Object body) {
        this.push2Users(group, users, protocol, CoreResponseCode.SUCCESS, body);
    }


    /**
     * 推送消息给用户流
     *
     * @param group    用户组
     * @param users    用户流
     * @param protocol 协议
     */
    public void push2Users(String group, Stream<? extends Identifiable> users, Protocol protocol, CommandResult message) {
        this.push2Users(group, users, protocol, message.getResultCode(), message.getBody());
    }

    /**
     * 推送消息给用户流
     *
     * @param group      用户组
     * @param users      用户流
     * @param resultCode 结果码
     * @param body       消息体
     */
    public void push2Users(String group, Stream<? extends Identifiable> users, ResultCode resultCode, Object body) {
        this.push2Users(group, users, ProtocolAide.PUSH, resultCode, body);
    }

    /**
     * 推送消息给用户流
     *
     * @param group 用户组
     * @param users 用户流
     * @param body  消息体
     */
    public void push2Users(String group, Stream<? extends Identifiable> users, Object body) {
        this.push2Users(group, users, ProtocolAide.PUSH, CoreResponseCode.SUCCESS, body);
    }

    /**
     * 推送消息给用户流
     *
     * @param group 用户组
     * @param users 用户流
     */
    public void push2Users(String group, Stream<? extends Identifiable> users, CommandResult message) {
        this.push2Users(group, users, ProtocolAide.PUSH, message.getResultCode(), message.getBody());
    }

    /**
     * 推送消息给用户流
     *
     * @param users      用户流
     * @param protocol   协议
     * @param resultCode 结果码
     * @param body       消息体
     */
    public void push2Users(Stream<? extends Identifiable> users, Protocol protocol, ResultCode resultCode, Object body) {
        this.sessionHolder.send2User(DEFAULT_USER_GROUP, users, toPush(protocol, resultCode, body));
    }


    /**
     * 推送消息给用户流
     *
     * @param users    用户流
     * @param protocol 协议
     * @param body     消息体
     */
    public void push2Users(Stream<? extends Identifiable> users, Protocol protocol, Object body) {
        this.push2Users(DEFAULT_USER_GROUP, users, protocol, CoreResponseCode.SUCCESS, body);
    }

    /**
     * 推送消息给用户流
     *
     * @param users    用户流
     * @param protocol 协议
     */
    public void push2Users(Stream<? extends Identifiable> users, Protocol protocol, CommandResult message) {
        this.push2Users(DEFAULT_USER_GROUP, users, protocol, message.getResultCode(), message.getBody());
    }

    /**
     * 推送消息给用户流
     *
     * @param users      用户流
     * @param resultCode 结果码
     * @param body       消息体
     */
    public void push2Users(Stream<? extends Identifiable> users, ResultCode resultCode, Object body) {
        this.push2Users(DEFAULT_USER_GROUP, users, ProtocolAide.PUSH, resultCode, body);
    }

    /**
     * 推送消息给用户流
     *
     * @param users 用户流
     * @param body  消息体
     */
    public void push2Users(Stream<? extends Identifiable> users, Object body) {
        this.push2Users(DEFAULT_USER_GROUP, users, ProtocolAide.PUSH, CoreResponseCode.SUCCESS, body);
    }


    /**
     * 推送消息给用户流
     *
     * @param users 用户流
     */
    public void push2Users(Stream<? extends Identifiable> users, CommandResult message) {
        this.push2Users(DEFAULT_USER_GROUP, users, ProtocolAide.PUSH, message.getResultCode(), message.getBody());
    }

    /**
     * 推送消息给所有在线
     *
     * @param group      用户组
     * @param protocol   协议
     * @param resultCode 结果码
     * @param body       消息体
     */
    public void push2Online(String group, Protocol protocol, ResultCode resultCode, Object body) {
        this.sessionHolder.send2AllOnline(group, toPush(protocol, resultCode, body));
    }


    /**
     * 推送消息给所有在线
     *
     * @param group    用户组
     * @param protocol 协议
     * @param body     消息体
     */
    public void push2Online(String group, Protocol protocol, Object body) {
        this.push2Online(group, protocol, CoreResponseCode.SUCCESS, body);
    }


    /**
     * 推送消息给所有在线
     *
     * @param group    用户组
     * @param protocol 协议
     */
    public void push2Online(String group, Protocol protocol, CommandResult message) {
        this.push2Online(group, protocol, message.getResultCode(), message.getBody());
    }

    /**
     * 推送消息给所有在线
     *
     * @param group      用户组
     * @param resultCode 结果码
     * @param body       消息体
     */
    public void push2Online(String group, ResultCode resultCode, Object body) {
        this.push2Online(group, ProtocolAide.PUSH, resultCode, body);
    }

    /**
     * 推送消息给所有在线
     *
     * @param group 用户组
     * @param body  消息体
     */
    public void push2Online(String group, Object body) {
        this.push2Online(group, ProtocolAide.PUSH, CoreResponseCode.SUCCESS, body);
    }

    /**
     * 推送消息给所有在线
     *
     * @param group 用户组
     */
    public void push2Online(String group, CommandResult message) {
        this.push2Online(group, ProtocolAide.PUSH, message.getResultCode(), message.getBody());
    }

    /**
     * 推送消息给所有在线
     *
     * @param protocol   协议
     * @param resultCode 结果码
     * @param body       消息体
     */
    public void push2Online(Protocol protocol, ResultCode resultCode, Object body) {
        this.sessionHolder.send2AllOnline(DEFAULT_USER_GROUP, toPush(protocol, resultCode, body));
    }


    /**
     * 推送消息给所有在线
     *
     * @param protocol 协议
     * @param body     消息体
     */
    public void push2Online(Protocol protocol, Object body) {
        this.push2Online(DEFAULT_USER_GROUP, protocol, CoreResponseCode.SUCCESS, body);
    }

    /**
     * 推送消息给所有在线
     *
     * @param protocol 协议
     */
    public void push2Online(Protocol protocol, CommandResult message) {
        this.push2Online(DEFAULT_USER_GROUP, protocol, message.getResultCode(), message.getBody());
    }

    /**
     * 推送消息给所有在线
     *
     * @param resultCode 结果码
     * @param body       消息体
     */
    public void push2Online(ResultCode resultCode, Object body) {
        this.push2Online(DEFAULT_USER_GROUP, ProtocolAide.PUSH, resultCode, body);
    }

    /**
     * 推送消息给所有在线
     *
     * @param body 消息体
     */
    public void push2Online(Object body) {
        this.push2Online(DEFAULT_USER_GROUP, ProtocolAide.PUSH, CoreResponseCode.SUCCESS, body);
    }


    /**
     * 推送消息给所有在线
     */
    public void push2Online(CommandResult message) {
        this.push2Online(DEFAULT_USER_GROUP, ProtocolAide.PUSH, message.getResultCode(), message.getBody());
    }


    /**
     * 指定用户 playerID 是否在线
     *
     * @param playerID 玩家ID
     * @return 返回ture 表示在线, 否则表示是下线
     */
    public boolean isOnline(long playerID) {
        return this.sessionHolder.isOnline(DEFAULT_USER_GROUP, playerID);
    }

    /**
     * @return 默认用户组session数量
     */
    public int size() {
        return this.sessionHolder.getSessionsByGroup(DEFAULT_USER_GROUP).size();
    }

    /**
     * @return 获取所有默认组用户session
     */
    public Set<Session> getAllUserSession() {
        return new HashSet<>(this.sessionHolder.getSessionsByGroup(DEFAULT_USER_GROUP).values());
    }

    /**
     * 将所有session下线
     */
    public void offlineAll() {
        this.sessionHolder.offlineAll(DEFAULT_USER_GROUP);
    }

    /**
     * 下线指定playerID的session
     *
     * @param playerID 指定玩家ID
     */
    public void offline(long playerID) {
        this.sessionHolder.offline(DEFAULT_USER_GROUP, playerID);
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

package com.tny.game.suite.net;

import com.tny.game.base.item.Identifiable;
import com.tny.game.common.result.ResultCode;
import com.tny.game.net.base.NetResultCode;
import com.tny.game.net.command.CommandResult;
import com.tny.game.net.message.*;
import com.tny.game.net.session.*;
import com.tny.game.net.transport.message.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Stream;

import static com.tny.game.net.transport.Certificates.*;
import static com.tny.game.suite.SuiteProfiles.*;

@Component
@Profile({SERVER, GAME})
public class SessionService {

    @Resource
    private SessionKeeperMannager sessionKeeperFactory;

    /**
     * 获取默认用户组中与指定uid对应的Session <br>
     *
     * @param uid 用户ID
     * @return 返回获取的session, 无session返回null
     */
    public <UID> Session<UID> getSession(UID uid) {
        return this.getSession(DEFAULT_USER_TYPE, uid);
    }


    /**
     * 获取userType用户组中与指定uid对应的Session <br>
     *
     * @param userType 用户组
     * @param uid      用户ID
     * @return 返回获取的session, 无session返回null
     */
    public <UID> Session<UID> getSession(String userType, UID uid) {
        SessionKeeper<UID> sessionKeeper = sessionKeeperFactory.getKeeper(userType);
        return sessionKeeper.getSession(uid);
    }


    /**
     * 推送消息给用户
     *
     * @param userType   用户组
     * @param uid        用户ID
     * @param protocol   协议
     * @param resultCode 结果码
     * @param body       消息体
     */
    public void pushByUID(String userType, Object uid, Protocol protocol, ResultCode resultCode, Object body) {
        SessionKeeper<Object> sessionKeeper = sessionKeeperFactory.getKeeper(userType);
        sessionKeeper.send2User(uid, toPush(protocol, resultCode, body));
    }


    /**
     * 推送消息给用户
     *
     * @param userType 用户组
     * @param uid      用户ID
     * @param protocol 协议
     * @param body     消息体
     */
    public void pushByUID(String userType, Object uid, Protocol protocol, Object body) {
        this.pushByUID(userType, uid, protocol, NetResultCode.SUCCESS, body);
    }


    /**
     * 推送消息给用户
     *
     * @param userType 用户组
     * @param uid      用户ID
     * @param protocol 协议
     */
    public void pushByUID(String userType, Object uid, Protocol protocol, CommandResult message) {
        this.pushByUID(userType, uid, protocol, message.getResultCode(), message.getBody());
    }

    /**
     * 推送消息给用户
     *
     * @param userType   用户组
     * @param uid        用户ID
     * @param resultCode 结果码
     * @param body       消息体
     */
    public void pushByUID(String userType, Object uid, ResultCode resultCode, Object body) {
        this.pushByUID(userType, uid, ProtocolAide.PUSH, resultCode, body);
    }

    /**
     * 推送消息给用户
     *
     * @param userType 用户组
     * @param uid      用户ID
     * @param body     消息体
     */
    public void pushByUID(String userType, Object uid, Object body) {
        this.pushByUID(userType, uid, ProtocolAide.PUSH, NetResultCode.SUCCESS, body);
    }

    /**
     * 推送消息给用户
     *
     * @param userType 用户组
     * @param uid      用户ID
     */
    public void pushByUID(String userType, Object uid, CommandResult message) {
        this.pushByUID(userType, uid, ProtocolAide.PUSH, message.getResultCode(), message.getBody());
    }

    /**
     * 推送消息给用户
     *
     * @param uid        用户ID
     * @param protocol   协议
     * @param resultCode 结果码
     * @param body       消息体
     */
    public void pushByUID(Object uid, Protocol protocol, ResultCode resultCode, Object body) {
        SessionKeeper<Object> sessionKeeper = sessionKeeperFactory.getKeeper(DEFAULT_USER_TYPE);
        sessionKeeper.send2User(uid, toPush(protocol, resultCode, body));
    }


    /**
     * 推送消息给用户
     *
     * @param uid      用户ID
     * @param protocol 协议
     * @param body     消息体
     */
    public void pushByUID(Object uid, Protocol protocol, Object body) {
        this.pushByUID(DEFAULT_USER_TYPE, uid, protocol, NetResultCode.SUCCESS, body);
    }

    /**
     * 推送消息给用户
     *
     * @param uid      用户ID
     * @param protocol 协议
     */
    public void pushByUID(Object uid, Protocol protocol, CommandResult message) {
        this.pushByUID(DEFAULT_USER_TYPE, uid, protocol, message.getResultCode(), message.getBody());
    }

    /**
     * 推送消息给用户
     *
     * @param uid        用户ID
     * @param resultCode 结果码
     * @param body       消息体
     */
    public void pushByUID(Object uid, ResultCode resultCode, Object body) {
        this.pushByUID(DEFAULT_USER_TYPE, uid, ProtocolAide.PUSH, resultCode, body);
    }

    /**
     * 推送消息给用户
     *
     * @param uid  用户ID
     * @param body 消息体
     */
    public void pushByUID(Object uid, Object body) {
        this.pushByUID(DEFAULT_USER_TYPE, uid, ProtocolAide.PUSH, NetResultCode.SUCCESS, body);
    }


    /**
     * 推送消息给用户
     *
     * @param uid 用户ID
     */
    public void pushByUID(Object uid, CommandResult message) {
        this.pushByUID(DEFAULT_USER_TYPE, uid, ProtocolAide.PUSH, message.getResultCode(), message.getBody());
    }

    /**
     * 推送消息给用户
     *
     * @param userType   用户组
     * @param user       可标识用户的对象
     * @param protocol   协议
     * @param resultCode 结果码
     * @param body       消息体
     */
    public void push2User(String userType, Identifiable user, Protocol protocol, ResultCode resultCode, Object body) {
        SessionKeeper<Object> sessionKeeper = sessionKeeperFactory.getKeeper(userType);
        sessionKeeper.send2User(user.getPlayerID(), toPush(protocol, resultCode, body));
    }


    /**
     * 推送消息给用户
     *
     * @param userType 用户组
     * @param user     可标识用户的对象
     * @param protocol 协议
     * @param body     消息体
     */
    public void push2User(String userType, Identifiable user, Protocol protocol, Object body) {
        this.push2User(userType, user, protocol, NetResultCode.SUCCESS, body);
    }


    /**
     * 推送消息给用户
     *
     * @param userType 用户组
     * @param user     可标识用户的对象
     * @param protocol 协议
     */
    public void push2User(String userType, Identifiable user, Protocol protocol, CommandResult message) {
        this.push2User(userType, user, protocol, message.getResultCode(), message.getBody());
    }

    /**
     * 推送消息给用户
     *
     * @param userType   用户组
     * @param user       可标识用户的对象
     * @param resultCode 结果码
     * @param body       消息体
     */
    public void push2User(String userType, Identifiable user, ResultCode resultCode, Object body) {
        this.push2User(userType, user, ProtocolAide.PUSH, resultCode, body);
    }

    /**
     * 推送消息给用户
     *
     * @param userType 用户组
     * @param user     可标识用户的对象
     * @param body     消息体
     */
    public void push2User(String userType, Identifiable user, Object body) {
        this.push2User(userType, user, ProtocolAide.PUSH, NetResultCode.SUCCESS, body);
    }

    /**
     * 推送消息给用户
     *
     * @param userType 用户组
     * @param user     可标识用户的对象
     */
    public void push2User(String userType, Identifiable user, CommandResult message) {
        this.push2User(userType, user, ProtocolAide.PUSH, message.getResultCode(), message.getBody());
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
        this.push2User(DEFAULT_USER_TYPE, user, protocol, resultCode, body);
    }


    /**
     * 推送消息给用户
     *
     * @param user     可标识用户的对象
     * @param protocol 协议
     * @param body     消息体
     */
    public void push2User(Identifiable user, Protocol protocol, Object body) {
        this.push2User(DEFAULT_USER_TYPE, user, protocol, NetResultCode.SUCCESS, body);
    }

    /**
     * 推送消息给用户
     *
     * @param user     可标识用户的对象
     * @param protocol 协议
     */
    public void push2User(Identifiable user, Protocol protocol, CommandResult message) {
        this.push2User(DEFAULT_USER_TYPE, user, protocol, message.getResultCode(), message.getBody());
    }

    /**
     * 推送消息给用户
     *
     * @param user       可标识用户的对象
     * @param resultCode 结果码
     * @param body       消息体
     */
    public void push2User(Identifiable user, ResultCode resultCode, Object body) {
        this.push2User(DEFAULT_USER_TYPE, user, ProtocolAide.PUSH, resultCode, body);
    }

    /**
     * 推送消息给用户
     *
     * @param user 可标识用户的对象
     * @param body 消息体
     */
    public void push2User(Identifiable user, Object body) {
        this.push2User(DEFAULT_USER_TYPE, user, ProtocolAide.PUSH, NetResultCode.SUCCESS, body);
    }


    /**
     * 推送消息给用户
     *
     * @param user 可标识用户的对象
     */
    public void push2User(Identifiable user, CommandResult message) {
        this.push2User(DEFAULT_USER_TYPE, user, ProtocolAide.PUSH, message.getResultCode(), message.getBody());
    }

    /**
     * 推送消息给用户流
     *
     * @param userType   用户组
     * @param users      用户流
     * @param protocol   协议
     * @param resultCode 结果码
     * @param body       消息体
     */
    public void push2Users(String userType, Stream<? extends Identifiable> users, Protocol protocol, ResultCode resultCode, Object body) {
        SessionKeeper<Object> sessionKeeper = sessionKeeperFactory.getKeeper(userType);
        sessionKeeper.send2Users(users.map(Identifiable::getPlayerID), toPush(protocol, resultCode, body));
    }


    /**
     * 推送消息给用户流
     *
     * @param userType 用户组
     * @param users    用户流
     * @param protocol 协议
     * @param body     消息体
     */
    public void push2Users(String userType, Stream<? extends Identifiable> users, Protocol protocol, Object body) {
        this.push2Users(userType, users, protocol, NetResultCode.SUCCESS, body);
    }


    /**
     * 推送消息给用户流
     *
     * @param userType 用户组
     * @param users    用户流
     * @param protocol 协议
     */
    public void push2Users(String userType, Stream<? extends Identifiable> users, Protocol protocol, CommandResult message) {
        this.push2Users(userType, users, protocol, message.getResultCode(), message.getBody());
    }

    /**
     * 推送消息给用户流
     *
     * @param userType   用户组
     * @param users      用户流
     * @param resultCode 结果码
     * @param body       消息体
     */
    public void push2Users(String userType, Stream<? extends Identifiable> users, ResultCode resultCode, Object body) {
        this.push2Users(userType, users, ProtocolAide.PUSH, resultCode, body);
    }

    /**
     * 推送消息给用户流
     *
     * @param userType 用户组
     * @param users    用户流
     * @param body     消息体
     */
    public void push2Users(String userType, Stream<? extends Identifiable> users, Object body) {
        this.push2Users(userType, users, ProtocolAide.PUSH, NetResultCode.SUCCESS, body);
    }

    /**
     * 推送消息给用户流
     *
     * @param userType 用户组
     * @param users    用户流
     */
    public void push2Users(String userType, Stream<? extends Identifiable> users, CommandResult message) {
        this.push2Users(userType, users, ProtocolAide.PUSH, message.getResultCode(), message.getBody());
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
        this.push2Users(DEFAULT_USER_TYPE, users, toPush(protocol, resultCode, body));

    }


    /**
     * 推送消息给用户流
     *
     * @param users    用户流
     * @param protocol 协议
     * @param body     消息体
     */
    public void push2Users(Stream<? extends Identifiable> users, Protocol protocol, Object body) {
        this.push2Users(DEFAULT_USER_TYPE, users, protocol, NetResultCode.SUCCESS, body);
    }

    /**
     * 推送消息给用户流
     *
     * @param users    用户流
     * @param protocol 协议
     */
    public void push2Users(Stream<? extends Identifiable> users, Protocol protocol, CommandResult message) {
        this.push2Users(DEFAULT_USER_TYPE, users, protocol, message.getResultCode(), message.getBody());
    }

    /**
     * 推送消息给用户流
     *
     * @param users      用户流
     * @param resultCode 结果码
     * @param body       消息体
     */
    public void push2Users(Stream<? extends Identifiable> users, ResultCode resultCode, Object body) {
        this.push2Users(DEFAULT_USER_TYPE, users, ProtocolAide.PUSH, resultCode, body);
    }

    /**
     * 推送消息给用户流
     *
     * @param users 用户流
     * @param body  消息体
     */
    public void push2Users(Stream<? extends Identifiable> users, Object body) {
        this.push2Users(DEFAULT_USER_TYPE, users, ProtocolAide.PUSH, NetResultCode.SUCCESS, body);
    }


    /**
     * 推送消息给用户流
     *
     * @param users 用户流
     */
    public void push2Users(Stream<? extends Identifiable> users, CommandResult message) {
        this.push2Users(DEFAULT_USER_TYPE, users, ProtocolAide.PUSH, message.getResultCode(), message.getBody());
    }

    /**
     * 推送消息给所有在线
     *
     * @param userType   用户组
     * @param protocol   协议
     * @param resultCode 结果码
     * @param body       消息体
     */
    public void push2Online(String userType, Protocol protocol, ResultCode resultCode, Object body) {
        SessionKeeper<Object> sessionKeeper = sessionKeeperFactory.getKeeper(userType);
        sessionKeeper.send2AllOnline(toPush(protocol, resultCode, body));
    }


    /**
     * 推送消息给所有在线
     *
     * @param userType 用户组
     * @param protocol 协议
     * @param body     消息体
     */
    public void push2Online(String userType, Protocol protocol, Object body) {
        this.push2Online(userType, protocol, NetResultCode.SUCCESS, body);
    }


    /**
     * 推送消息给所有在线
     *
     * @param userType 用户组
     * @param protocol 协议
     */
    public void push2Online(String userType, Protocol protocol, CommandResult message) {
        this.push2Online(userType, protocol, message.getResultCode(), message.getBody());
    }

    /**
     * 推送消息给所有在线
     *
     * @param userType   用户组
     * @param resultCode 结果码
     * @param body       消息体
     */
    public void push2Online(String userType, ResultCode resultCode, Object body) {
        this.push2Online(userType, ProtocolAide.PUSH, resultCode, body);
    }

    /**
     * 推送消息给所有在线
     *
     * @param userType 用户组
     * @param body     消息体
     */
    public void push2Online(String userType, Object body) {
        this.push2Online(userType, ProtocolAide.PUSH, NetResultCode.SUCCESS, body);
    }

    /**
     * 推送消息给所有在线
     *
     * @param userType 用户组
     */
    public void push2Online(String userType, CommandResult message) {
        this.push2Online(userType, ProtocolAide.PUSH, message.getResultCode(), message.getBody());
    }

    /**
     * 推送消息给所有在线
     *
     * @param protocol   协议
     * @param resultCode 结果码
     * @param body       消息体
     */
    public void push2Online(Protocol protocol, ResultCode resultCode, Object body) {
        this.push2Online(DEFAULT_USER_TYPE, toPush(protocol, resultCode, body));
    }


    /**
     * 推送消息给所有在线
     *
     * @param protocol 协议
     * @param body     消息体
     */
    public void push2Online(Protocol protocol, Object body) {
        this.push2Online(DEFAULT_USER_TYPE, protocol, NetResultCode.SUCCESS, body);
    }

    /**
     * 推送消息给所有在线
     *
     * @param protocol 协议
     */
    public void push2Online(Protocol protocol, CommandResult message) {
        this.push2Online(DEFAULT_USER_TYPE, protocol, message.getResultCode(), message.getBody());
    }

    /**
     * 推送消息给所有在线
     *
     * @param resultCode 结果码
     * @param body       消息体
     */
    public void push2Online(ResultCode resultCode, Object body) {
        this.push2Online(DEFAULT_USER_TYPE, ProtocolAide.PUSH, resultCode, body);
    }

    /**
     * 推送消息给所有在线
     *
     * @param body 消息体
     */
    public void push2Online(Object body) {
        this.push2Online(DEFAULT_USER_TYPE, ProtocolAide.PUSH, NetResultCode.SUCCESS, body);
    }


    /**
     * 推送消息给所有在线
     */
    public void push2Online(CommandResult message) {
        this.push2Online(DEFAULT_USER_TYPE, ProtocolAide.PUSH, message.getResultCode(), message.getBody());
    }


    /**
     * 指定用户 playerID 是否在线
     *
     * @param playerID 玩家ID
     * @return 返回ture 表示在线, 否则表示是下线
     */
    public boolean isOnline(long playerID) {
        return this.isOnline(DEFAULT_USER_TYPE, playerID);
    }


    /**
     * 指定用户 playerID 是否在线
     *
     * @param userType 玩家ID
     * @param playerID 玩家ID
     * @return 返回ture 表示在线, 否则表示是下线
     */
    public boolean isOnline(String userType, long playerID) {
        SessionKeeper<Object> sessionKeeper = sessionKeeperFactory.getKeeper(userType);
        return sessionKeeper.isOnline(playerID);
    }

    /**
     * @return 默认用户组session数量
     */
    public int getSessionSize() {
        return this.getSessionSize(DEFAULT_USER_TYPE);
    }

    /**
     * @param userType 玩家ID
     * @return 默认用户组session数量
     */
    public int getSessionSize(String userType) {
        SessionKeeper<Object> sessionKeeper = sessionKeeperFactory.getKeeper(userType);
        return sessionKeeper.size();
    }

    /**
     * @return 获取所有默认组用户session
     */
    public Set<Session> getAllSession() {
        return this.getAllSession(DEFAULT_USER_TYPE);
    }

    /**
     * @return 获取所有默认组用户session
     */
    public Set<Session> getAllSession(String userType) {
        SessionKeeper<Object> sessionKeeper = sessionKeeperFactory.getKeeper(userType);
        return new HashSet<>(sessionKeeper.getAllSessions().values());
    }

    /**
     * 将默认组所有session下线
     */
    public void offlineAll() {
        this.offlineAll(DEFAULT_USER_TYPE);
    }

    /**
     * 将指定用户组所有session下线
     *
     * @param userType 用户组
     */
    public void offlineAll(String userType) {
        SessionKeeper<Object> sessionKeeper = sessionKeeperFactory.getKeeper(userType);
        sessionKeeper.offlineAll();
    }

    /**
     * 下线默认组中指定playerID的session
     *
     * @param playerID 指定玩家ID
     */
    public void offline(long playerID) {
        this.offline(DEFAULT_USER_TYPE, playerID);
    }

    /**
     * 下线默认组中指定playerID的session
     *
     * @param playerID 指定玩家ID
     */
    public void offline(String userType, Object playerID) {
        SessionKeeper<Object> sessionKeeper = sessionKeeperFactory.getKeeper(userType);
        sessionKeeper.offline(playerID);
    }

}

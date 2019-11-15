package com.tny.game.suite.net;

import com.google.common.collect.ImmutableList;
import com.tny.game.base.item.*;
import com.tny.game.common.result.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Stream;

import static com.tny.game.net.transport.Certificates.*;
import static com.tny.game.suite.SuiteProfiles.*;

@Component
@Profile({SERVER, GAME})
public class EndpointService {

    @Resource
    private EndpointKeeperManager endpointKeeperManager;

    /**
     * 获取默认用户组中与指定uid对应的Endpoint <br>
     *
     * @param uid 用户ID
     * @return 返回获取的session, 无session返回null
     */
    public <UID> Optional<Endpoint<UID>> getEndpoint(UID uid) {
        return this.getEndpoint(DEFAULT_USER_TYPE, uid);
    }


    /**
     * 获取userType用户组中与指定uid对应的Endpoint <br>
     *
     * @param userType 用户组
     * @param uid      用户ID
     * @return 返回获取的session, 无session返回null
     */
    public <UID> Optional<Endpoint<UID>> getEndpoint(String userType, UID uid) {
        Optional<EndpointKeeper<UID, Endpoint<UID>>> endpointKeeperOpt = endpointKeeperManager.getKeeper(userType);
        return endpointKeeperOpt.map(k -> k.getEndpoint(uid));
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
    public void pushByUid(String userType, Object uid, Protocol protocol, ResultCode resultCode, Object body) {
        Optional<EndpointKeeper<Object, Endpoint<Object>>> keeperOpt = endpointKeeperManager.getKeeper(userType);
        keeperOpt.ifPresent(k -> k.send2User(uid, toPush(protocol, resultCode, body)));
    }

    private MessageContext<Object> toPush(Protocol protocol, ResultCode resultCode, Object body) {
        return MessageContexts
                .push(protocol, resultCode, body);
    }


    /**
     * 推送消息给用户
     *
     * @param userType 用户组
     * @param uid      用户ID
     * @param protocol 协议
     * @param body     消息体
     */
    public void pushByUid(String userType, Object uid, Protocol protocol, Object body) {
        this.pushByUid(userType, uid, protocol, NetResultCode.SUCCESS, body);
    }


    /**
     * 推送消息给用户
     *
     * @param userType 用户组
     * @param uid      用户ID
     * @param protocol 协议
     */
    public void pushByUid(String userType, Object uid, Protocol protocol, CommandResult message) {
        this.pushByUid(userType, uid, protocol, message.getResultCode(), message.getBody());
    }

    /**
     * 推送消息给用户
     *
     * @param userType   用户组
     * @param uid        用户ID
     * @param resultCode 结果码
     * @param body       消息体
     */
    public void pushByUid(String userType, Object uid, ResultCode resultCode, Object body) {
        this.pushByUid(userType, uid, ProtocolAide.PUSH, resultCode, body);
    }

    /**
     * 推送消息给用户
     *
     * @param userType 用户组
     * @param uid      用户ID
     * @param body     消息体
     */
    public void pushByUid(String userType, Object uid, Object body) {
        this.pushByUid(userType, uid, ProtocolAide.PUSH, NetResultCode.SUCCESS, body);
    }

    /**
     * 推送消息给用户
     *
     * @param userType 用户组
     * @param uid      用户ID
     */
    public void pushByUid(String userType, Object uid, CommandResult message) {
        this.pushByUid(userType, uid, ProtocolAide.PUSH, message.getResultCode(), message.getBody());
    }

    /**
     * 推送消息给用户
     *
     * @param uid        用户ID
     * @param protocol   协议
     * @param resultCode 结果码
     * @param body       消息体
     */
    public void pushByUid(Object uid, Protocol protocol, ResultCode resultCode, Object body) {
        Optional<EndpointKeeper<Object, Endpoint<Object>>> keeperOpt = endpointKeeperManager.getKeeper(DEFAULT_USER_TYPE);
        keeperOpt.ifPresent(k -> k.send2User(uid, toPush(protocol, resultCode, body)));
    }


    /**
     * 推送消息给用户
     *
     * @param uid      用户ID
     * @param protocol 协议
     * @param body     消息体
     */
    public void pushByUid(Object uid, Protocol protocol, Object body) {
        this.pushByUid(DEFAULT_USER_TYPE, uid, protocol, NetResultCode.SUCCESS, body);
    }

    /**
     * 推送消息给用户
     *
     * @param uid      用户ID
     * @param protocol 协议
     */
    public void pushByUid(Object uid, Protocol protocol, CommandResult message) {
        this.pushByUid(DEFAULT_USER_TYPE, uid, protocol, message.getResultCode(), message.getBody());
    }

    /**
     * 推送消息给用户
     *
     * @param uid        用户ID
     * @param resultCode 结果码
     * @param body       消息体
     */
    public void pushByUid(Object uid, ResultCode resultCode, Object body) {
        this.pushByUid(DEFAULT_USER_TYPE, uid, ProtocolAide.PUSH, resultCode, body);
    }

    /**
     * 推送消息给用户
     *
     * @param uid  用户ID
     * @param body 消息体
     */
    public void pushByUid(Object uid, Object body) {
        this.pushByUid(DEFAULT_USER_TYPE, uid, ProtocolAide.PUSH, NetResultCode.SUCCESS, body);
    }


    /**
     * 推送消息给用户
     *
     * @param uid 用户ID
     */
    public void pushByUid(Object uid, CommandResult message) {
        this.pushByUid(DEFAULT_USER_TYPE, uid, ProtocolAide.PUSH, message.getResultCode(), message.getBody());
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
    public void push2User(String userType, Identifier user, Protocol protocol, ResultCode resultCode, Object body) {
        Optional<EndpointKeeper<Object, Endpoint<Object>>> keeperOpt = endpointKeeperManager.getKeeper(userType);
        keeperOpt.ifPresent(k -> k.send2User(user.getPlayerId(), toPush(protocol, resultCode, body)));
    }


    /**
     * 推送消息给用户
     *
     * @param userType 用户组
     * @param user     可标识用户的对象
     * @param protocol 协议
     * @param body     消息体
     */
    public void push2User(String userType, Identifier user, Protocol protocol, Object body) {
        this.push2User(userType, user, protocol, NetResultCode.SUCCESS, body);
    }


    /**
     * 推送消息给用户
     *
     * @param userType 用户组
     * @param user     可标识用户的对象
     * @param protocol 协议
     */
    public void push2User(String userType, Identifier user, Protocol protocol, CommandResult message) {
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
    public void push2User(String userType, Identifier user, ResultCode resultCode, Object body) {
        this.push2User(userType, user, ProtocolAide.PUSH, resultCode, body);
    }

    /**
     * 推送消息给用户
     *
     * @param userType 用户组
     * @param user     可标识用户的对象
     * @param body     消息体
     */
    public void push2User(String userType, Identifier user, Object body) {
        this.push2User(userType, user, ProtocolAide.PUSH, NetResultCode.SUCCESS, body);
    }

    /**
     * 推送消息给用户
     *
     * @param userType 用户组
     * @param user     可标识用户的对象
     */
    public void push2User(String userType, Identifier user, CommandResult message) {
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
    public void push2User(Identifier user, Protocol protocol, ResultCode resultCode, Object body) {
        this.push2User(DEFAULT_USER_TYPE, user, protocol, resultCode, body);
    }


    /**
     * 推送消息给用户
     *
     * @param user     可标识用户的对象
     * @param protocol 协议
     * @param body     消息体
     */
    public void push2User(Identifier user, Protocol protocol, Object body) {
        this.push2User(DEFAULT_USER_TYPE, user, protocol, NetResultCode.SUCCESS, body);
    }

    /**
     * 推送消息给用户
     *
     * @param user     可标识用户的对象
     * @param protocol 协议
     */
    public void push2User(Identifier user, Protocol protocol, CommandResult message) {
        this.push2User(DEFAULT_USER_TYPE, user, protocol, message.getResultCode(), message.getBody());
    }

    /**
     * 推送消息给用户
     *
     * @param user       可标识用户的对象
     * @param resultCode 结果码
     * @param body       消息体
     */
    public void push2User(Identifier user, ResultCode resultCode, Object body) {
        this.push2User(DEFAULT_USER_TYPE, user, ProtocolAide.PUSH, resultCode, body);
    }

    /**
     * 推送消息给用户
     *
     * @param user 可标识用户的对象
     * @param body 消息体
     */
    public void push2User(Identifier user, Object body) {
        this.push2User(DEFAULT_USER_TYPE, user, ProtocolAide.PUSH, NetResultCode.SUCCESS, body);
    }


    /**
     * 推送消息给用户
     *
     * @param user 可标识用户的对象
     */
    public void push2User(Identifier user, CommandResult message) {
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
    public void push2Users(String userType, Stream<? extends Identifier> users, Protocol protocol, ResultCode resultCode, Object body) {
        Optional<EndpointKeeper<Object, Endpoint<Object>>> keeperOpt = endpointKeeperManager.getKeeper(userType);
        keeperOpt.ifPresent(k -> k.send2Users(users.map(Identifier::getPlayerId), toPush(protocol, resultCode, body)));
    }


    /**
     * 推送消息给用户流
     *
     * @param userType 用户组
     * @param users    用户流
     * @param protocol 协议
     * @param body     消息体
     */
    public void push2Users(String userType, Stream<? extends Identifier> users, Protocol protocol, Object body) {
        this.push2Users(userType, users, protocol, NetResultCode.SUCCESS, body);
    }


    /**
     * 推送消息给用户流
     *
     * @param userType 用户组
     * @param users    用户流
     * @param protocol 协议
     */
    public void push2Users(String userType, Stream<? extends Identifier> users, Protocol protocol, CommandResult message) {
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
    public void push2Users(String userType, Stream<? extends Identifier> users, ResultCode resultCode, Object body) {
        this.push2Users(userType, users, ProtocolAide.PUSH, resultCode, body);
    }

    /**
     * 推送消息给用户流
     *
     * @param userType 用户组
     * @param users    用户流
     * @param body     消息体
     */
    public void push2Users(String userType, Stream<? extends Identifier> users, Object body) {
        this.push2Users(userType, users, ProtocolAide.PUSH, NetResultCode.SUCCESS, body);
    }

    /**
     * 推送消息给用户流
     *
     * @param userType 用户组
     * @param users    用户流
     */
    public void push2Users(String userType, Stream<? extends Identifier> users, CommandResult message) {
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
    public void push2Users(Stream<? extends Identifier> users, Protocol protocol, ResultCode resultCode, Object body) {
        this.push2Users(DEFAULT_USER_TYPE, users, toPush(protocol, resultCode, body));

    }


    /**
     * 推送消息给用户流
     *
     * @param users    用户流
     * @param protocol 协议
     * @param body     消息体
     */
    public void push2Users(Stream<? extends Identifier> users, Protocol protocol, Object body) {
        this.push2Users(DEFAULT_USER_TYPE, users, protocol, NetResultCode.SUCCESS, body);
    }

    /**
     * 推送消息给用户流
     *
     * @param users    用户流
     * @param protocol 协议
     */
    public void push2Users(Stream<? extends Identifier> users, Protocol protocol, CommandResult message) {
        this.push2Users(DEFAULT_USER_TYPE, users, protocol, message.getResultCode(), message.getBody());
    }

    /**
     * 推送消息给用户流
     *
     * @param users      用户流
     * @param resultCode 结果码
     * @param body       消息体
     */
    public void push2Users(Stream<? extends Identifier> users, ResultCode resultCode, Object body) {
        this.push2Users(DEFAULT_USER_TYPE, users, ProtocolAide.PUSH, resultCode, body);
    }

    /**
     * 推送消息给用户流
     *
     * @param users 用户流
     * @param body  消息体
     */
    public void push2Users(Stream<? extends Identifier> users, Object body) {
        this.push2Users(DEFAULT_USER_TYPE, users, ProtocolAide.PUSH, NetResultCode.SUCCESS, body);
    }


    /**
     * 推送消息给用户流
     *
     * @param users 用户流
     */
    public void push2Users(Stream<? extends Identifier> users, CommandResult message) {
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
        Optional<EndpointKeeper<Object, Endpoint<Object>>> keeperOpt = endpointKeeperManager.getKeeper(userType);
        keeperOpt.ifPresent(k -> k.send2AllOnline(toPush(protocol, resultCode, body)));
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
     * @return 默认用户组session数量
     */
    public int getEndpointSize() {
        return this.getEndpointSize(DEFAULT_USER_TYPE);
    }

    /**
     * @param userType 玩家ID
     * @return 默认用户组session数量
     */
    public int getEndpointSize(String userType) {
        Optional<EndpointKeeper<Object, Endpoint<Object>>> keeperOpt = endpointKeeperManager.getKeeper(userType);
        return keeperOpt.map(EndpointKeeper::size).orElse(0);
    }

    /**
     * @return 获取所有默认组用户session
     */
    public <UID> Set<Endpoint<UID>> getAllEndpoint() {
        return this.getAllEndpoint(DEFAULT_USER_TYPE);
    }

    /**
     * @return 获取所有默认组用户session
     */
    public <UID> Set<Endpoint<UID>> getAllEndpoint(String userType) {
        Optional<EndpointKeeper<UID, Endpoint<UID>>> keeperOpt = endpointKeeperManager.getKeeper(userType);
        Collection<Endpoint<UID>> endpoints = keeperOpt.map(k -> k.getAllEndpoints().values()).orElseGet(ImmutableList::of);
        return new HashSet<>(endpoints);
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
        Optional<SessionKeeper<Object>> keeperOpt = endpointKeeperManager.getSessionKeeper(userType);
        keeperOpt.ifPresent(SessionKeeper::offlineAll);
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
        Optional<SessionKeeper<Object>> keeperOpt = endpointKeeperManager.getSessionKeeper(userType);
        keeperOpt.ifPresent(k -> k.offline(playerID));
    }

    /**
     * 指定用户 playerId 是否在线
     *
     * @param playerID 玩家ID
     * @return 返回 true 表示在线, 否则表示是下线
     */
    public boolean isOnline(long playerID) {
        return this.isOnline(DEFAULT_USER_TYPE, playerID);
    }

    /**
     * 指定用户 playerId 是否在线
     *
     * @param userType 玩家ID
     * @param playerID 玩家ID
     * @return true 表示在线, 否则表示是下线
     */
    public boolean isOnline(String userType, long playerID) {
        Optional<SessionKeeper<Object>> keeperOpt = endpointKeeperManager.getSessionKeeper(userType);
        return keeperOpt.map(k -> k.isOnline(playerID)).orElse(false);
    }

}

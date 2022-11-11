/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.tny.game.net.endpoint;

import com.google.common.collect.ImmutableList;
import com.tny.game.common.result.*;
import com.tny.game.net.base.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

import java.util.*;
import java.util.stream.Stream;

public class EndpointService {

    private final EndpointKeeperManager endpointKeeperManager;

    public EndpointService(EndpointKeeperManager endpointKeeperManager) {
        this.endpointKeeperManager = endpointKeeperManager;
    }

    /**
     * 获取messagerType用户类型中与指定uid对应的Endpoint <br>
     *
     * @param messagerType 用户类型
     * @param uid          用户ID
     * @return 返回获取的session, 无session返回null
     */
    public <UID> Optional<Endpoint<UID>> getEndpoint(MessagerType messagerType, UID uid) {
        Optional<EndpointKeeper<UID, Endpoint<UID>>> endpointKeeperOpt = this.endpointKeeperManager.getKeeper(messagerType);
        return endpointKeeperOpt.map(k -> k.getEndpoint(uid));
    }

    /**
     * 推送消息给用户
     *
     * @param messagerType 用户类型
     * @param uid          用户ID
     * @param protocol     协议
     * @param resultCode   结果码
     * @param body         消息体
     */
    public void pushByUid(MessagerType messagerType, Object uid, Protocol protocol, ResultCode resultCode, Object body) {
        Optional<EndpointKeeper<Object, Endpoint<Object>>> keeperOpt = this.endpointKeeperManager.getKeeper(messagerType);
        keeperOpt.ifPresent(k -> k.send2User(uid, toPush(protocol, resultCode, body)));
    }

    private MessageContent toPush(Protocol protocol, ResultCode resultCode, Object body) {
        return MessageContents
                .push(protocol, resultCode, body);
    }

    /**
     * 推送消息给用户
     *
     * @param messagerType 用户类型
     * @param uid          用户ID
     * @param protocol     协议
     * @param body         消息体
     */
    public void pushByUid(MessagerType messagerType, Object uid, Protocol protocol, Object body) {
        this.pushByUid(messagerType, uid, protocol, NetResultCode.SUCCESS, body);
    }

    /**
     * 推送消息给用户
     *
     * @param messagerType 用户类型
     * @param uid          用户ID
     * @param protocol     协议
     */
    public void pushByUid(MessagerType messagerType, Object uid, Protocol protocol, RpcResult<?> message) {
        this.pushByUid(messagerType, uid, protocol, message.resultCode(), message.getBody());
    }

    /**
     * 推送消息给用户
     *
     * @param messagerType 用户类型
     * @param uid          用户ID
     * @param resultCode   结果码
     * @param body         消息体
     */
    public void pushByUid(MessagerType messagerType, Object uid, ResultCode resultCode, Object body) {
        this.pushByUid(messagerType, uid, Protocols.PUSH, resultCode, body);
    }

    /**
     * 推送消息给用户
     *
     * @param messagerType 用户类型
     * @param uid          用户ID
     * @param body         消息体
     */
    public void pushByUid(MessagerType messagerType, Object uid, Object body) {
        this.pushByUid(messagerType, uid, Protocols.PUSH, NetResultCode.SUCCESS, body);
    }

    /**
     * 推送消息给用户
     *
     * @param messagerType 用户类型
     * @param uid          用户ID
     */
    public void pushByUid(MessagerType messagerType, Object uid, RpcResult<?> message) {
        this.pushByUid(messagerType, uid, Protocols.PUSH, message.resultCode(), message.getBody());
    }

    /**
     * 推送消息给用户
     *
     * @param user       可标识用户的对象
     * @param protocol   协议
     * @param resultCode 结果码
     * @param body       消息体
     */
    public void push2User(Messager user, Protocol protocol, ResultCode resultCode, Object body) {
        Optional<EndpointKeeper<Object, Endpoint<Object>>> keeperOpt = this.endpointKeeperManager.getKeeper(user.getMessagerType());
        keeperOpt.ifPresent(k -> k.send2User(user.getMessagerId(), toPush(protocol, resultCode, body)));
    }

    /**
     * 推送消息给用户
     *
     * @param user     可标识用户的对象
     * @param protocol 协议
     * @param body     消息体
     */
    public void push2User(Messager user, Protocol protocol, Object body) {
        this.push2User(user, protocol, NetResultCode.SUCCESS, body);
    }

    /**
     * 推送消息给用户
     *
     * @param user     可标识用户的对象
     * @param protocol 协议
     */
    public void push2User(Messager user, Protocol protocol, RpcResult<?> message) {
        this.push2User(user, protocol, message.resultCode(), message.getBody());
    }

    /**
     * 推送消息给用户
     *
     * @param user       可标识用户的对象
     * @param resultCode 结果码
     * @param body       消息体
     */
    public void push2User(Messager user, ResultCode resultCode, Object body) {
        this.push2User(user, Protocols.PUSH, resultCode, body);
    }

    /**
     * 推送消息给用户
     *
     * @param user 可标识用户的对象
     * @param body 消息体
     */
    public void push2User(Messager user, Object body) {
        this.push2User(user, Protocols.PUSH, NetResultCode.SUCCESS, body);
    }

    /**
     * 推送消息给用户
     *
     * @param user 可标识用户的对象
     */
    public void push2User(Messager user, RpcResult<?> message) {
        this.push2User(user, Protocols.PUSH, message.resultCode(), message.getBody());
    }

    /**
     * 推送消息给用户流
     *
     * @param users      用户流
     * @param protocol   协议
     * @param resultCode 结果码
     * @param body       消息体
     */
    public void push2Users(Stream<? extends Messager> users, Protocol protocol, ResultCode resultCode, Object body) {
        users.forEach(user -> this.push2User(user, toPush(protocol, resultCode, body)));
    }

    /**
     * 推送消息给用户流
     *
     * @param users    用户流
     * @param protocol 协议
     * @param body     消息体
     */
    public void push2Users(Stream<? extends Messager> users, Protocol protocol, Object body) {
        this.push2Users(users, protocol, NetResultCode.SUCCESS, body);
    }

    /**
     * 推送消息给用户流
     *
     * @param users    用户流
     * @param protocol 协议
     */
    public void push2Users(Stream<? extends Messager> users, Protocol protocol, RpcResult<?> message) {
        this.push2Users(users, protocol, message.resultCode(), message.getBody());
    }

    /**
     * 推送消息给用户流
     *
     * @param users      用户流
     * @param resultCode 结果码
     * @param body       消息体
     */
    public void push2Users(Stream<? extends Messager> users, ResultCode resultCode, Object body) {
        this.push2Users(users, Protocols.PUSH, resultCode, body);
    }

    /**
     * 推送消息给用户流
     *
     * @param users 用户流
     * @param body  消息体
     */
    public void push2Users(Stream<? extends Messager> users, Object body) {
        this.push2Users(users, Protocols.PUSH, NetResultCode.SUCCESS, body);
    }

    /**
     * 推送消息给用户流
     *
     * @param users 用户流
     */
    public void push2Users(Stream<? extends Messager> users, RpcResult<?> message) {
        this.push2Users(users, Protocols.PUSH, message.resultCode(), message.getBody());
    }

    /**
     * 推送消息给所有在线
     *
     * @param messagerType 用户类型
     * @param protocol     协议
     * @param resultCode   结果码
     * @param body         消息体
     */
    public void push2Online(MessagerType messagerType, Protocol protocol, ResultCode resultCode, Object body) {
        Optional<EndpointKeeper<Object, Endpoint<Object>>> keeperOpt = this.endpointKeeperManager.getKeeper(messagerType);
        keeperOpt.ifPresent(k -> k.send2AllOnline(toPush(protocol, resultCode, body)));
    }

    /**
     * 推送消息给所有在线
     *
     * @param messagerType 用户类型
     * @param protocol     协议
     * @param body         消息体
     */
    public void push2Online(MessagerType messagerType, Protocol protocol, Object body) {
        this.push2Online(messagerType, protocol, NetResultCode.SUCCESS, body);
    }

    /**
     * 推送消息给所有在线
     *
     * @param messagerType 用户类型
     * @param protocol     协议
     */
    public void push2Online(MessagerType messagerType, Protocol protocol, RpcResult<?> message) {
        this.push2Online(messagerType, protocol, message.resultCode(), message.getBody());
    }

    /**
     * 推送消息给所有在线
     *
     * @param messagerType 用户类型
     * @param resultCode   结果码
     * @param body         消息体
     */
    public void push2Online(MessagerType messagerType, ResultCode resultCode, Object body) {
        this.push2Online(messagerType, Protocols.PUSH, resultCode, body);
    }

    /**
     * 推送消息给所有在线
     *
     * @param messagerType 用户类型
     * @param body         消息体
     */
    public void push2Online(MessagerType messagerType, Object body) {
        this.push2Online(messagerType, Protocols.PUSH, NetResultCode.SUCCESS, body);
    }

    /**
     * 推送消息给所有在线
     *
     * @param messagerType 用户类型
     */
    public void push2Online(MessagerType messagerType, RpcResult<?> message) {
        this.push2Online(messagerType, Protocols.PUSH, message.resultCode(), message.getBody());
    }

    /**
     * @param messagerType 玩家ID
     * @return 默认用户类型session数量
     */
    public int getEndpointSize(MessagerType messagerType) {
        Optional<EndpointKeeper<Object, Endpoint<Object>>> keeperOpt = this.endpointKeeperManager.getKeeper(messagerType);
        return keeperOpt.map(EndpointKeeper::size).orElse(0);
    }

    /**
     * @return 获取所有默认组用户session
     */
    public <UID> Set<Endpoint<UID>> getAllEndpoint(MessagerType messagerType) {
        Optional<EndpointKeeper<UID, Endpoint<UID>>> keeperOpt = this.endpointKeeperManager.getKeeper(messagerType);
        Collection<Endpoint<UID>> endpoints = keeperOpt.map(k -> k.getAllEndpoints().values()).orElseGet(ImmutableList::of);
        return new HashSet<>(endpoints);
    }

    /**
     * 将指定用户类型所有session下线
     *
     * @param messagerType 用户类型
     */
    public void offlineAll(MessagerType messagerType) {
        Optional<SessionKeeper<Object>> keeperOpt = this.endpointKeeperManager.getSessionKeeper(messagerType);
        keeperOpt.ifPresent(SessionKeeper::offlineAll);
    }

    /**
     * 下线默认组中指定playerId的session
     *
     * @param playerId 指定玩家ID
     */
    public void offline(MessagerType messagerType, Object playerId) {
        Optional<SessionKeeper<Object>> keeperOpt = this.endpointKeeperManager.getSessionKeeper(messagerType);
        keeperOpt.ifPresent(k -> k.offline(playerId));
    }

    /**
     * 指定用户 playerId 是否在线
     *
     * @param messagerType 玩家ID
     * @param playerId     玩家ID
     * @return true 表示在线, 否则表示是下线
     */
    public boolean isOnline(MessagerType messagerType, long playerId) {
        Optional<SessionKeeper<Object>> keeperOpt = this.endpointKeeperManager.getSessionKeeper(messagerType);
        return keeperOpt.map(k -> k.isOnline(playerId)).orElse(false);
    }

}

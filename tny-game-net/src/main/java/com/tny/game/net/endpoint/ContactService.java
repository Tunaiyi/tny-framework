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
import com.tny.game.net.application.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

import java.util.*;
import java.util.stream.Stream;

public class ContactService {

    private final EndpointKeeperManager endpointKeeperManager;

    public ContactService(EndpointKeeperManager endpointKeeperManager) {
        this.endpointKeeperManager = endpointKeeperManager;
    }

    /**
     * 获取contactType用户类型中与指定identify对应的Endpoint <br>
     *
     * @param contactType 用户类型
     * @param identify    用户ID
     * @return 返回获取的session, 无session返回null
     */
    public Optional<Endpoint> getEndpoint(ContactType contactType, long identify) {
        Optional<EndpointKeeper<Endpoint>> endpointKeeperOpt = this.endpointKeeperManager.getKeeper(contactType);
        return endpointKeeperOpt.map(k -> k.getEndpoint(identify));
    }

    /**
     * 推送消息给用户
     *
     * @param contactType 用户类型
     * @param identify    用户ID
     * @param protocol    协议
     * @param resultCode  结果码
     * @param body        消息体
     */
    public void pushByUid(ContactType contactType, long identify, Protocol protocol, ResultCode resultCode, Object body) {
        Optional<EndpointKeeper<Endpoint>> keeperOpt = this.endpointKeeperManager.getKeeper(contactType);
        keeperOpt.ifPresent(k -> k.send2User(identify, toPush(protocol, resultCode, body)));
    }

    private MessageContent toPush(Protocol protocol, ResultCode resultCode, Object body) {
        return MessageContents
                .push(protocol, resultCode, body);
    }

    /**
     * 推送消息给用户
     *
     * @param contactType 用户类型
     * @param identify    用户ID
     * @param protocol    协议
     * @param body        消息体
     */
    public void pushByUid(ContactType contactType, long identify, Protocol protocol, Object body) {
        this.pushByUid(contactType, identify, protocol, NetResultCode.SUCCESS, body);
    }

    /**
     * 推送消息给用户
     *
     * @param contactType 用户类型
     * @param identify    用户ID
     * @param protocol    协议
     */
    public void pushByUid(ContactType contactType, long identify, Protocol protocol, RpcResult<?> message) {
        this.pushByUid(contactType, identify, protocol, message.resultCode(), message.getBody());
    }

    /**
     * 推送消息给用户
     *
     * @param contactType 用户类型
     * @param identify    用户ID
     * @param resultCode  结果码
     * @param body        消息体
     */
    public void pushByUid(ContactType contactType, long identify, ResultCode resultCode, Object body) {
        this.pushByUid(contactType, identify, Protocols.PUSH, resultCode, body);
    }

    /**
     * 推送消息给用户
     *
     * @param contactType 用户类型
     * @param identify    用户ID
     * @param body        消息体
     */
    public void pushByUid(ContactType contactType, long identify, Object body) {
        this.pushByUid(contactType, identify, Protocols.PUSH, NetResultCode.SUCCESS, body);
    }

    /**
     * 推送消息给用户
     *
     * @param contactType 用户类型
     * @param identify    用户ID
     */
    public void pushByUid(ContactType contactType, long identify, RpcResult<?> message) {
        this.pushByUid(contactType, identify, Protocols.PUSH, message.resultCode(), message.getBody());
    }

    /**
     * 推送消息给用户
     *
     * @param user       可标识用户的对象
     * @param protocol   协议
     * @param resultCode 结果码
     * @param body       消息体
     */
    public void push2User(Contact user, Protocol protocol, ResultCode resultCode, Object body) {
        Optional<EndpointKeeper<Endpoint>> keeperOpt = this.endpointKeeperManager.getKeeper(user.getContactType());
        keeperOpt.ifPresent(k -> k.send2User(user.getContactId(), toPush(protocol, resultCode, body)));
    }

    /**
     * 推送消息给用户
     *
     * @param user     可标识用户的对象
     * @param protocol 协议
     * @param body     消息体
     */
    public void push2User(Contact user, Protocol protocol, Object body) {
        this.push2User(user, protocol, NetResultCode.SUCCESS, body);
    }

    /**
     * 推送消息给用户
     *
     * @param user     可标识用户的对象
     * @param protocol 协议
     */
    public void push2User(Contact user, Protocol protocol, RpcResult<?> message) {
        this.push2User(user, protocol, message.resultCode(), message.getBody());
    }

    /**
     * 推送消息给用户
     *
     * @param user       可标识用户的对象
     * @param resultCode 结果码
     * @param body       消息体
     */
    public void push2User(Contact user, ResultCode resultCode, Object body) {
        this.push2User(user, Protocols.PUSH, resultCode, body);
    }

    /**
     * 推送消息给用户
     *
     * @param user 可标识用户的对象
     * @param body 消息体
     */
    public void push2User(Contact user, Object body) {
        this.push2User(user, Protocols.PUSH, NetResultCode.SUCCESS, body);
    }

    /**
     * 推送消息给用户
     *
     * @param user 可标识用户的对象
     */
    public void push2User(Contact user, RpcResult<?> message) {
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
    public void push2Users(Stream<? extends Contact> users, Protocol protocol, ResultCode resultCode, Object body) {
        users.forEach(user -> this.push2User(user, toPush(protocol, resultCode, body)));
    }

    /**
     * 推送消息给用户流
     *
     * @param users    用户流
     * @param protocol 协议
     * @param body     消息体
     */
    public void push2Users(Stream<? extends Contact> users, Protocol protocol, Object body) {
        this.push2Users(users, protocol, NetResultCode.SUCCESS, body);
    }

    /**
     * 推送消息给用户流
     *
     * @param users    用户流
     * @param protocol 协议
     */
    public void push2Users(Stream<? extends Contact> users, Protocol protocol, RpcResult<?> message) {
        this.push2Users(users, protocol, message.resultCode(), message.getBody());
    }

    /**
     * 推送消息给用户流
     *
     * @param users      用户流
     * @param resultCode 结果码
     * @param body       消息体
     */
    public void push2Users(Stream<? extends Contact> users, ResultCode resultCode, Object body) {
        this.push2Users(users, Protocols.PUSH, resultCode, body);
    }

    /**
     * 推送消息给用户流
     *
     * @param users 用户流
     * @param body  消息体
     */
    public void push2Users(Stream<? extends Contact> users, Object body) {
        this.push2Users(users, Protocols.PUSH, NetResultCode.SUCCESS, body);
    }

    /**
     * 推送消息给用户流
     *
     * @param users 用户流
     */
    public void push2Users(Stream<? extends Contact> users, RpcResult<?> message) {
        this.push2Users(users, Protocols.PUSH, message.resultCode(), message.getBody());
    }

    /**
     * 推送消息给所有在线
     *
     * @param contactType 用户类型
     * @param protocol    协议
     * @param resultCode  结果码
     * @param body        消息体
     */
    public void push2Online(ContactType contactType, Protocol protocol, ResultCode resultCode, Object body) {
        Optional<EndpointKeeper<Endpoint>> keeperOpt = this.endpointKeeperManager.getKeeper(contactType);
        keeperOpt.ifPresent(k -> k.send2AllOnline(toPush(protocol, resultCode, body)));
    }

    /**
     * 推送消息给所有在线
     *
     * @param contactType 用户类型
     * @param protocol    协议
     * @param body        消息体
     */
    public void push2Online(ContactType contactType, Protocol protocol, Object body) {
        this.push2Online(contactType, protocol, NetResultCode.SUCCESS, body);
    }

    /**
     * 推送消息给所有在线
     *
     * @param contactType 用户类型
     * @param protocol    协议
     */
    public void push2Online(ContactType contactType, Protocol protocol, RpcResult<?> message) {
        this.push2Online(contactType, protocol, message.resultCode(), message.getBody());
    }

    /**
     * 推送消息给所有在线
     *
     * @param contactType 用户类型
     * @param resultCode  结果码
     * @param body        消息体
     */
    public void push2Online(ContactType contactType, ResultCode resultCode, Object body) {
        this.push2Online(contactType, Protocols.PUSH, resultCode, body);
    }

    /**
     * 推送消息给所有在线
     *
     * @param contactType 用户类型
     * @param body        消息体
     */
    public void push2Online(ContactType contactType, Object body) {
        this.push2Online(contactType, Protocols.PUSH, NetResultCode.SUCCESS, body);
    }

    /**
     * 推送消息给所有在线
     *
     * @param contactType 用户类型
     */
    public void push2Online(ContactType contactType, RpcResult<?> message) {
        this.push2Online(contactType, Protocols.PUSH, message.resultCode(), message.getBody());
    }

    /**
     * @param contactType 玩家ID
     * @return 默认用户类型session数量
     */
    public int getEndpointSize(ContactType contactType) {
        Optional<EndpointKeeper<Endpoint>> keeperOpt = this.endpointKeeperManager.getKeeper(contactType);
        return keeperOpt.map(EndpointKeeper::size).orElse(0);
    }

    /**
     * @return 获取所有默认组用户session
     */
    public Set<Endpoint> getAllEndpoint(ContactType contactType) {
        Optional<EndpointKeeper<Endpoint>> keeperOpt = this.endpointKeeperManager.getKeeper(contactType);
        Collection<Endpoint> endpoints = keeperOpt.map(k -> k.getAllEndpoints().values()).orElseGet(ImmutableList::of);
        return new HashSet<>(endpoints);
    }

    /**
     * 将指定用户类型所有session下线
     *
     * @param contactType 用户类型
     */
    public void offlineAll(ContactType contactType) {
        Optional<SessionKeeper> keeperOpt = this.endpointKeeperManager.getSessionKeeper(contactType);
        keeperOpt.ifPresent(SessionKeeper::offlineAll);
    }

    /**
     * 下线默认组中指定playerId的session
     *
     * @param identify 指定玩家ID
     */
    public void offline(ContactType contactType, long identify) {
        Optional<SessionKeeper> keeperOpt = this.endpointKeeperManager.getSessionKeeper(contactType);
        keeperOpt.ifPresent(k -> k.offline(identify));
    }

    /**
     * 指定用户 playerId 是否在线
     *
     * @param contactType 玩家ID
     * @param playerId    玩家ID
     * @return true 表示在线, 否则表示是下线
     */
    public boolean isOnline(ContactType contactType, long playerId) {
        Optional<SessionKeeper> keeperOpt = this.endpointKeeperManager.getSessionKeeper(contactType);
        return keeperOpt.map(k -> k.isOnline(playerId)).orElse(false);
    }

}

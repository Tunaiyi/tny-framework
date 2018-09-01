package com.tny.game.net.session;

import com.google.common.collect.Range;
import com.tny.game.common.event.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.Message;
import com.tny.game.net.session.listener.SessionListener;
import com.tny.game.net.tunnel.*;

import java.util.List;

/**
 * Created by Kun Yang on 2017/2/16.
 */
public interface NetSession<UID> extends Session<UID> {

    @SuppressWarnings("unchecked")
    BindP1EventBus<SessionListener, Session, Tunnel> ON_ONLINE =
            EventBuses.of(SessionListener.class, SessionListener::onOnline);

    @SuppressWarnings("unchecked")
    BindP1EventBus<SessionListener, Session, Tunnel> ON_OFFLINE =
            EventBuses.of(SessionListener.class, SessionListener::onOffline);

    @SuppressWarnings("unchecked")
    BindP1EventBus<SessionListener, Session, Tunnel> ON_CLOSE =
            EventBuses.of(SessionListener.class, SessionListener::onClose);

    /**
     * @return 弹出输入事件
     */
    SessionInputEvent<UID> pollInputEvent();

    /**
     * @return 弹出输出事件
     */
    SessionOutputEvent<UID> pollOutputEvent();

    /**
     * 通过响应ID寻找已发送消息
     *
     * @param toMessageID 响应ID
     * @return 返回消息
     */
    Message<UID> getSentMessageByToID(int toMessageID);

    /**
     * 获取指定范围的已发送消息
     *
     * @param range 指定范围
     * @return 获取消息列表
     */
    List<Message<UID>> getSentMessages(Range<Integer> range);

    /**
     * @return 是否有输入事件
     */
    boolean isHasInputEvent();

    /**
     * @return 是否有输出事件
     */
    boolean isHasOutputEvent();

    /**
     * @return 获取输入事件数量
     */
    int getInputEventSize();

    /**
     * @return 获取输出事件数量
     */
    int getOutputEventSize();

    /**
     * 使用指定认证登陆
     *
     * @param certificate 指定认证
     */
    void login(NetCertificate<UID> certificate) throws ValidatorFailException;

    /**
     * 使用指定认证登陆
     *
     * @param session 指定认证
     */
    void mergeSession(NetSession<UID> session) throws ValidatorFailException;

    /**
     * 接收消息
     *
     * @param tunnel  消息通道
     * @param message 消息
     */
    void receive(NetTunnel<UID> tunnel, Message<UID> message);

    /**
     * 发送消息
     *
     * @param tunnel  消息通道
     * @param content 消息内容
     */
    void send(NetTunnel<UID> tunnel, MessageContent<UID> content);

    /**
     * 重新messageID消息
     *
     * @param message 重发消息
     */
    void resend(NetTunnel<UID> tunnel, ResendMessage<UID> message);

    /**
     * @return 获取当前通道
     */
    @Override
    NetTunnel<UID> getCurrentTunnel();

    /**
     * 发送消息
     *
     * @param event 事件
     */
    void write(SessionSendEvent<UID> event) throws TunnelWriteException;

    /**
     * session下线, 不立即失效
     */
    void offline();

    /**
     * 使下线如果当前通道为指定通道
     *
     * @param tunnel 指定通道
     */
    boolean offlineIfCurrent(Tunnel<UID> tunnel);

}

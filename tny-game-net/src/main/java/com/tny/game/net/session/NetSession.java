package com.tny.game.net.session;

import com.google.common.collect.Range;
import com.tny.game.net.exception.ValidatorFailException;
import com.tny.game.net.message.Message;
import com.tny.game.net.message.MessageContent;
import com.tny.game.net.session.event.SessionInputEvent;
import com.tny.game.net.session.event.SessionOutputEvent;
import com.tny.game.net.tunnel.NetTunnel;
import com.tny.game.net.tunnel.Tunnel;

import java.util.List;

/**
 * Created by Kun Yang on 2017/2/16.
 */
public interface NetSession<UID> extends Session<UID> {

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
    Message<UID> getHandledSendEventByToID(int toMessageID);

    /**
     * 获取指定范围的已发送消息
     *
     * @param range 指定范围
     * @return 获取消息列表
     */
    List<Message<UID>> getHandledSendEvents(Range<Integer> range);

    /**
     * @return 是否有输入事件
     */
    boolean hasInputEvent();

    /**
     * @return 是否有输出事件
     */
    boolean hasOutputEvent();

    /**
     * 使用指定认证登陆
     *
     * @param certificate 指定认证
     */
    void login(LoginCertificate<UID> certificate) throws ValidatorFailException;

    /**
     * 使用指定认证登陆
     *
     * @param session 指定认证
     */
    boolean relogin(NetSession<UID> session);

    /**
     * @return 获取当前通道
     */
    @Override
    NetTunnel<UID> getCurrentTunnel();

    /**
     * 根据内容创建消息
     *
     * @param tunnel  发送终端
     * @param content 内容
     * @return 消息
     */
    Message<UID> createMessage(Tunnel<UID> tunnel, MessageContent<?> content);
}

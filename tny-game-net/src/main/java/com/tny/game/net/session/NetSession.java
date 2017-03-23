package com.tny.game.net.session;

import com.google.common.collect.Range;
import com.tny.game.net.LoginCertificate;
import com.tny.game.net.message.MessageBuilderFactory;
import com.tny.game.net.session.event.SessionInputEvent;
import com.tny.game.net.session.event.SessionOutputEvent;
import com.tny.game.net.session.event.SessionSendEvent;

import java.util.List;

/**
 * Created by Kun Yang on 2017/2/16.
 */
public interface NetSession<UID> extends Session<UID> {

    /**
     * @return 弹出输入事件
     */
    SessionInputEvent pollInputEvent();

    /**
     * @return 弹出输出事件
     */
    SessionOutputEvent pollOutputEvent();

    /**
     * 获取指定范围的已处理发送事件
     *
     * @param range 指定方位
     * @return 获取事件列表
     */
    List<SessionSendEvent> getHandledSendEvents(Range<Integer> range);

    /**
     * @return 是否有输入事件
     */
    boolean hasInputEvent();

    /**
     * @return 是否有输出事件
     */
    boolean hasOutputEvent();

    /**
     * @return 消息构建器工厂
     */
    MessageBuilderFactory getMessageBuilderFactory();

    /**
     * 通过指定session使当前session恢复上线.
     *
     * @param session 指定session
     * @return 返回是否上线成功
     */
    boolean transferFrom(NetSession<UID> session);

    /**
     * 使session失效
     *
     * @return 失效返回true 失效返回false
     */
    boolean invalid();

    /**
     * 使用指定认证登陆
     *
     * @param certificate 指定认证
     */
    void login(LoginCertificate<UID> certificate);

    /**
     * 写出数据指定事件
     *
     * @param event 写出事件
     */
    void write(SessionSendEvent event);

}

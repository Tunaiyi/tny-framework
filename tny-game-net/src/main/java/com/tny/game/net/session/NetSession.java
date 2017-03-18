package com.tny.game.net.session;

import com.tny.game.net.LoginCertificate;
import com.tny.game.net.message.MessageBuilderFactory;
import com.tny.game.net.session.event.SessionInputEvent;
import com.tny.game.net.session.event.SessionOutputEvent;

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
     * @return 是否有输入事件
     */
    boolean hasInputEvent();

    /**
     * @return 是否有输出事件
     */
    boolean hasOutputEvent();

    MessageBuilderFactory getMessageBuilderFactory();

    MessageFuture<?> takeFuture(int id);

    /**
     * 通过指定session使当前session恢复上线.
     *
     * @param session 指定session
     * @return 返回是否上线成功
     */
    boolean reline(NetSession<UID> session);

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

}

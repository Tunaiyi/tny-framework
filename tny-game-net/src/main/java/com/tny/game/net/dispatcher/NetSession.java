package com.tny.game.net.dispatcher;

import com.tny.game.net.LoginCertificate;

public interface NetSession extends Session {

    void login(LoginCertificate loginInfo);

    /**
     * 断开连接
     */
    void disconnect();

    // /**
    //  * 获取信息构建器工厂
    //  *
    //  * @return
    //  */
    // MessageBuilderFactory getMessageBuilderFactory();

}
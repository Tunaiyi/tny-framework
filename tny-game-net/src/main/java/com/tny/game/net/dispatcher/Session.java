package com.tny.game.net.dispatcher;

import com.tny.game.common.context.Attributes;
import com.tny.game.net.LoginCertificate;

/**
 * 用户会话对象 此对象从Socket链接便创建,保存用户链接后的属性对象,直到Socket断开连接
 *
 * @author KGTny
 */
public interface Session {

    int DEFAULT_RESPONSE_ID = -1;
    long UN_LOGIN_UID = 0;
    String DEFAULT_USER_GROUP = "USER";
    String UNLOGIN_USER_GROUP = "UNLOGIN";

    /**
     * 客户端用户ID
     *
     * @return
     */
    long getUID();

    /**
     * 客户端用户组名称
     * <p>
     * <p>
     * <br>
     *
     * @return
     */
    String getGroup();

    /**
     * 客户端登陆时间
     *
     * @return
     */
    long getLoginAt();

    /**
     * 客户端是否登錄
     * <p>
     * <p>
     * <br>
     *
     * @return
     */
    boolean isAskerLogin();

    /**
     * 獲取响应的IP地址
     * <p>
     * <p>
     * 獲取請求的IP地址<br>
     *
     * @return 返回IP地址
     */
    String getHostName();

    /**
     * 获取会话属性
     *
     * @return
     */
    Attributes attributes();

    /**
     * 是否已连接
     *
     * @return 连接返回true 否则返回false
     */
    boolean isConnect();

    LoginCertificate getCertificate();

    boolean isOnline();

}

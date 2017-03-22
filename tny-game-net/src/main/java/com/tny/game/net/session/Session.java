package com.tny.game.net.session;

import com.tny.game.common.context.Attributes;
import com.tny.game.net.LoginCertificate;
import com.tny.game.net.message.Message;
import com.tny.game.net.message.MessageContent;
import com.tny.game.net.message.Protocol;
import org.joda.time.DateTime;

/**
 * 用户会话对象 此对象从Socket链接便创建,保存用户链接后的属性对象,直到Socket断开连接
 *
 * @author KGTny
 */
public interface Session<UID> {

    // int DEFAULT_RESPONSE_ID = -1;
    // long UN_LOGIN_UID = 0;
    String DEFAULT_USER_GROUP = "USER";
    String UNLOGIN_USER_GROUP = "UNLOGIN";

    /**
     * 客户端用户ID
     *
     * @return
     */
    UID getUID();

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
    DateTime getLoginAt();

    /**
     * 客户端是否登錄
     * <p>
     * <p>
     * <br>
     *
     * @return
     */
    boolean isLogin();

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
     * 是否已上线
     *
     * @return 连接返回true 否则返回false
     */
    boolean isOnline();

    /**
     * 是否是失效,无法重连
     *
     * @return 失效返回true 失效返回false
     */
    boolean isInvalided();

    /**
     * @return 登陆凭证
     */
    LoginCertificate<UID> getCertificate();

    /**
     * session下线
     *
     * @param invalid 是否立即失效
     */
    void offline(boolean invalid);


    /**
     * session下线, 不立即失效
     */
    default void offline() {
        offline(false);
    }

    /**
     * @return 获取下线时间
     */
    long getOfflineTime();

    /**
     * @return 最后接受消息时间
     */
    long getLastReceiveTime();

    /**
     * 发送消息
     *
     * @param protocol 协议
     * @param content  消息内容
     */
    void sendMessage(Protocol protocol, MessageContent content);

    /**
     * 接收消息
     *
     * @param message 消息
     */
    void receiveMessage(Message<UID> message);

    /**
     * 重新messageID消息
     *
     * @param messageID 消息ID
     */
    void resendMessage(int messageID);

    /**
     * 重新发送从fromID开始的消息
     *
     * @param fromID 消息ID
     */
    void resendMessages(int fromID);

    /**
     * 重新发送从fromID开始到toID的消息
     *
     * @param fromID 消息ID
     */
    void resendMessages(int fromID, int toID);

}

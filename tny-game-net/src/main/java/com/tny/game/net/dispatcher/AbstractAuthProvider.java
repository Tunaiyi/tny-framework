package com.tny.game.net.dispatcher;

import com.tny.game.net.LoginCertificate;
import com.tny.game.net.dispatcher.exception.DispatchException;

/**
 * @author KGTny
 * @ClassName: AbstractSessionValidator
 * @Description: 抽象登录验证器
 * @date 2011-10-11 下午6:28:09
 * <p>
 * 抽象登录验证器
 * <p>
 * 实现对请求身份的校验<br>
 */
public abstract class AbstractAuthProvider implements AuthProvider {

    public AbstractAuthProvider() {
    }

    @Override
    public LoginCertificate validate(Request request) throws DispatchException {
        Integer userID = this.checkUserID(request);
        if (userID == null) {
            LoginCertificate info = this.checkSystemID(request);
            if (info != null)
                return info;
        } else if (userID != null && userID > Session.UN_LOGIN_UID) {
            return LoginCertificate.createLogin(userID, false);
        }
        return LoginCertificate.createUnLogin();
    }

    /**
     * 普通用户校验
     * <p>
     * <p>
     * 若校验成功返回Uid,失败返回null<br>
     *
     * @param request 登录请求
     * @param channel 通道
     * @return 校验成功返回Session, 失败返回null
     */
    protected abstract LoginCertificate checkSystemID(Request request);

    /**
     * 普通用户校验
     * <p>
     * <p>
     * 若校验成功返回Uid,失败返回null<br>
     *
     * @param request 登录请求
     * @param channel 通道
     * @return 校验成功返回Session, 失败返回null
     * @throws DispatchException
     */
    protected abstract Integer checkUserID(Request request) throws DispatchException;

}

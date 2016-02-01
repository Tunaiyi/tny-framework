package com.tny.game.net.dispatcher;

import com.tny.game.net.LoginCertificate;
import com.tny.game.net.dispatcher.exception.DispatchException;

/**
 * Session登录验证器
 *
 * @author KGTny
 */
public interface AuthProvider {

    /**
     * @return 获取名字
     */
    default String getName() {
        return "AuthProvider";
    }

    /**
     * 验证Session登录, 返回带有验证结果的Session对象
     *
     * @param request 与Session相对应的链接通道
     * @return 带有验证结果的Session对象
     * @throws DispatchException
     */
    LoginCertificate validate(Request request) throws DispatchException;

    /**
     * 是否可以进行登录校验
     *
     * @param request 校验的请求
     * @return 返回是否可以
     */
    boolean isCanValidate(Request request);

}

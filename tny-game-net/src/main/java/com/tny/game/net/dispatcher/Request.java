package com.tny.game.net.dispatcher;

import com.tny.game.common.context.Attributes;
import com.tny.game.net.base.NetMessage;

import java.io.Serializable;
import java.util.List;

/**
 * 请求对象接口
 *
 * @author KGTny
 */
public abstract class Request extends NetMessage implements Serializable {

    public static final long serialVersionUID = 1L;

    /**
     * 获取请求时间
     * <p>
     * <p>
     * 获取请求时间 <br>
     *
     * @return 返回请求时间
     */
    public abstract long getTime();

    /**
     * 请求有效期
     * <p>
     * <p>
     * 请求是否在有效期<br>
     *
     * @return 如果有效返回true 失败返回false
     */
    public abstract boolean isTimeOut(long duration);

    /**
     * 请求参数对象
     *
     * @param <T>
     * @param clazz
     * @return
     */
    public abstract <T> T getParameter(int index, Class<T> clazz);

    /**
     * 请求参数对象
     *
     * @param clazzArray
     * @return
     */
    public abstract Object[] getParameters(Class<?>[] clazzArray);

    /**
     * 用户ID 无用户ID返回-1
     *
     * @return
     */
    public abstract long getUserID();

    /**
     * 獲取所属用户组
     *
     * @return
     */
    public abstract String getUserGroup();

    /**
     * 是否是登录请求
     *
     * @return
     */
    public abstract boolean isLogin();

    /**
     * 获取参数长度
     *
     * @return
     */
    public abstract int size();

    /**
     * 获取请求IP
     *
     * @return IP
     */
    public abstract String getHostName();

    // /**
    // * 獲取請求的IP地址
    // * <p>
    // *
    // * 獲取請求的IP地址<br>
    // *
    // * @return 返回IP地址
    // */
    // public String getHostName();

    /**
     * 获取请求参数列表
     * <p>
     * <p>
     * 获取请求参数列表<br>
     *
     * @return 返回请求参数列表
     */
    public abstract List<Object> getParamList();

    /**
     * 校验码
     *
     * @return
     */
    public abstract String getCheckKey();

    /**
     * 获取请求属性
     *
     * @return
     */
    public abstract Attributes attributes();

    /**
     * request对象中是否有属性
     *
     * @return
     */
    public abstract boolean isHasAttributes();

    /**
     * 设置session
     *
     * @param session
     */
    protected abstract void owner(Session session);

}

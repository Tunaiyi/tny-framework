package com.tny.game.net.endpoint;

import com.tny.game.common.url.*;

/**
 * 用户会话对象 此对象从Socket链接便创建,保存用户链接后的属性对象,直到Socket断开连接
 *
 * @author KGTny
 */
public interface Terminal<UID> extends Endpoint<UID> {

    /**
     * @return 获取客户端 url
     */
    URL getUrl();

}

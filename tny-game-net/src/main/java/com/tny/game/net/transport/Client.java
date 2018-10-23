package com.tny.game.net.transport;

import com.tny.game.common.utils.URL;
import com.tny.game.net.session.Session;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-10 15:22
 */
public interface Client<UID> extends Session<UID> {

    /**
     * @return 获取客户端 url
     */
    URL getUrl();

}

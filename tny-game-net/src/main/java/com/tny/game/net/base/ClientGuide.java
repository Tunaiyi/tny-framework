package com.tny.game.net.base;

import com.tny.game.common.utils.URL;
import com.tny.game.net.endpoint.Client;
import com.tny.game.net.transport.*;

/**
 * Created by Kun Yang on 2017/3/28.
 */
public interface ClientGuide {


    /**
     * @return 是否关闭
     */
    boolean isClosed();

    /**
     * @return 关闭
     */
    boolean close();

    /**
     * @param url         url
     * @param certificate 登录凭证
     * @param postConnect 连接后处理
     * @param <UID>
     * @return
     */
    <UID> Client<UID> connect(URL url, Certificate<UID> certificate, PostConnect<UID> postConnect);

    /**
     * @param url         url
     * @param certificate 登录凭证
     * @param <UID>       * @return
     */
    default <UID> Client<UID> connect(URL url, Certificate<UID> certificate) {
        return connect(url, certificate, null);
    }

}

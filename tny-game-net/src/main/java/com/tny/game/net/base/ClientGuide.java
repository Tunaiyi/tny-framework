package com.tny.game.net.base;

import com.tny.game.common.utils.*;
import com.tny.game.net.endpoint.*;

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
     * @param postConnect 连接后处理
     * @param <UID>
     * @return
     */
    <UID> Client<UID> connect(URL url, UID unloginUid, PostConnect<UID> postConnect);

    /**
     * @param url   url
     * @param <UID> * @return
     */
    default <UID> Client<UID> connect(URL url, UID unloginUid) {
        return connect(url, unloginUid, null);
    }

    /**
     * @param url         url
     * @param postConnect 连接后处理
     * @param <UID>
     * @return
     */
    default <UID> Client<UID> connect(URL url, PostConnect<UID> postConnect) {
        return connect(url, null, postConnect);
    }

    /**
     * @param url   url
     * @param <UID> * @return
     */
    default <UID> Client<UID> connect(URL url) {
        return connect(url, null);
    }

}

/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.base;

import com.tny.game.common.url.*;
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
     * @return 返回客户端
     */
    <UID> Client<UID> client(URL url, PostConnect<UID> postConnect);

    /**
     * @param url   url
     * @param <UID> * @return
     */
    default <UID> Client<UID> client(URL url) {
        return client(url, null);
    }

    ClientBootstrapSetting getSetting();

}

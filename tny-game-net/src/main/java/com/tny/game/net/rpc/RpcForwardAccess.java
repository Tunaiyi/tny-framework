/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.tny.game.net.rpc;

import com.tny.game.net.application.*;
import com.tny.game.net.message.*;

/**
 * 转发接入
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/25 20:11
 **/
public interface RpcForwardAccess extends RpcAccess {

    /**
     * @return 服务 id
     */
    RpcAccessIdentify getRpcIdentify();

    /**
     * @return 获取转发服务者
     */
    ForwardPoint getForwardPoint();

}

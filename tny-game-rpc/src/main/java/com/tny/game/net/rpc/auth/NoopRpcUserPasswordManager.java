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

package com.tny.game.net.rpc.auth;

import com.tny.game.net.application.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/5 1:55 上午
 */
public class NoopRpcUserPasswordManager implements RpcUserPasswordManager {

    @Override
    public boolean auth(RpcAccessIdentify identify, String password) {
        return true;
    }

}

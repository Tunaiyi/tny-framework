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

import com.tny.game.common.result.*;
import com.tny.game.net.application.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/4 5:39 下午
 */
public interface RpcAuthService {

    DoneResult<RpcAccessIdentify> authenticate(long id, String password);

    String createToken(RpcServiceType serviceType, RpcAccessIdentify removeIdentify);

    DoneResult<RpcAccessToken> verifyToken(String token);

}

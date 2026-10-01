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
package com.tny.game.it.assembly.topology.caller;

import com.tny.game.demo.core.common.*;
import com.tny.game.demo.core.common.dto.*;
import com.tny.game.net.annotation.*;
import com.tny.game.net.rpc.annotation.*;

/**
 * F 族转发探针代理（design D7，任务 3.1）：目标终端为 demo game 的
 * {@code SPEAK$SAY_FOR_RPC}（返回 "respond "+message，天然携带终端到达标记），
 * 首跳经 forwardService 指向转发应用的 rpc.server 服务名。
 */
@RpcRemoteService(value = "game-service", forwardService = "gateway-service")
public interface ForwardProbeService {

    @RpcBody
    @RpcRequest(CtrlerIds.SPEAK$SAY_FOR_RPC)
    SayContentDTO sayForBody(String message);

}

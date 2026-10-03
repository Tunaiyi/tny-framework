/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

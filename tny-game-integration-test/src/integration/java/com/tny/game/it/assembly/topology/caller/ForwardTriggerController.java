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

import com.tny.game.demo.core.common.dto.*;
import com.tny.game.net.annotation.*;
import org.springframework.beans.factory.annotation.*;

/**
 * F 族触发控制器（任务 3.1）：玩家以测试协议 90001 到达 caller 后，
 * 经 {@link ForwardProbeService} 代理同步转发；终端返回的 SayContentDTO
 * （"respond fwd-&lt;tag&gt;"）原样回执给玩家——消息即链路证明。
 */
@RpcController
public class ForwardTriggerController {

    /** 测试域协议号，避开 demo CtrlerIds 段位。 */
    public static final int TRIGGER = 90001;

    @Autowired
    private ForwardProbeService probe;

    @RpcRequest(TRIGGER)
    public SayContentDTO trigger(@RpcParam String tag) {
        return probe.sayForBody("fwd-" + tag);
    }

}

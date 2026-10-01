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

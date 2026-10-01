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
package com.tny.game.it.net;

import com.tny.game.it.harness.*;
import com.tny.game.it.harness.NetIntegrationHarness.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

/**
 * 编解码全实现参数化往返矩阵（specs R4"多实现参数化往返"，追溯 {@code net-protocol} 规格；
 * add-integration-testing 任务 4.3）。
 * <p>
 * TCP 报文体编解码当前实现为 jprotobuf（TypeProtobufMessageBodyCodec）与 protoex
 * （ProtoExMessageBodyCodec）两种（实施纠偏：jackson 属 ObjectCodec 层，非报文体编解码，
 * 不入本矩阵）。同一往返场景对每个实现各执行一遍。
 */
@Tag("integration")
class TcpCodecMatrixIT {

    @ParameterizedTest(name = "报文体编解码={0}")
    @EnumSource(BodyCodecKind.class)
    void roundTripAcrossAllBodyCodecs(BodyCodecKind kind) throws Exception {
        try (NetIntegrationHarness harness = NetIntegrationHarness.start(kind)) {
            RoundTripScenario.assertRoundTrip(harness, "tny-codec-matrix-" + kind.name());
        }
    }

}

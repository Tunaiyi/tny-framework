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

package com.tny.game.demo.net;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.tny.game.codec.typeprotobuf.*;
import com.tny.game.demo.core.common.dto.*;

import java.io.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2022/1/25 1:33 PM
 */
public class TestMain {

    private static final TypeProtobufSchemeManager schemeManager = TypeProtobufSchemeManager.getInstance();

    public static void main(String[] args) throws IOException {
        schemeManager.loadScheme(SayContentDTO.class);
        TypeProtobufObjectCodecFactory codecFactory = new TypeProtobufObjectCodecFactory();
        byte[] data = new byte[]{
                -27, -106, -104, 0, 8, 2, 18, 29, 100, 101, 108, 97, 121, 32, 109, 101, 115, 115, 97, 103, 101, 32, 58, 32, 106, 100, 106, 97, 108,
                106, 102, 100, 106, 97, 102, 107, 97, 116, 32, 49, 54, 52, 51, 48, 57, 56, 50, 48, 52, 56, 50, 51, 24, -72, -96, 54};
        TypeProtobufScheme<SayContentDTO> scheme = schemeManager.loadScheme(SayContentDTO.class);
        Codec<SayContentDTO> codec = scheme.getCodec();
        //		ObjectCodec<?> codec = codecFactory.createCodec(null);
        try (ByteArrayInputStream buffInput = new ByteArrayInputStream(data, 0, data.length)) {
            //			Object value = codec.decode(buffInput);
            Object value = codec.decode(data);
            System.out.println(value);
        }

    }

}

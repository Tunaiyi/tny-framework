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

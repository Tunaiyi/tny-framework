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

package protoex.test;

import com.tny.game.protoex.*;
import org.junit.jupiter.api.*;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

class ProtoExIOStreamTest {

    private final byte[][] bytesValue = {"abcdefghijklmnopqlstuvwxyz".getBytes(), "ABCDEFGHIJKLMNOPQLSTUVWXYZ".getBytes()};

    @Test
    void testReadBuff() {
        byte[] data;
        try (ProtoExOutputStream outputStream = new ProtoExOutputStream()) {
            for (byte[] array : bytesValue) {
                outputStream.writeBytes(array);
            }
            data = outputStream.toByteArray();
        }
        System.out.println(data.length);
        try (ProtoExInputStream inputStream = new ProtoExInputStream(data)) {
            for (byte[] array : bytesValue) {
                ByteBuffer buffer = inputStream.readBuffer();
                byte[] check = new byte[array.length];
                buffer.get(check);
                assertArrayEquals(array, check);
            }
        }

    }

}

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

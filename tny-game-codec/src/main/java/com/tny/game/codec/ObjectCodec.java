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

package com.tny.game.codec;

import java.io.*;
import java.util.Base64;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2020/9/10 11:52 上午
 */
public interface ObjectCodec<T> {

    boolean isPlaintext();

    byte[] encode(T value) throws IOException;

    void encode(T value, OutputStream output) throws IOException;

    T decode(byte[] bytes) throws IOException;

    T decode(InputStream input) throws IOException;

    default String format(T value) throws IOException {
        return formatBytes(encode(value));
    }

    default T parse(String data) throws IOException {
        return decode(parseBytes(data));
    }

    default String formatBytes(byte[] data) {
        if (data == null) {
            return null;
        }
        if (isPlaintext()) {
            return new String(data, CoderCharsets.DEFAULT);
        } else {
            return Base64.getUrlEncoder().encodeToString(data);
        }
    }

    default byte[] parseBytes(String data) {
        if (data == null) {
            return null;
        }
        if (isPlaintext()) {
            return data.getBytes(CoderCharsets.DEFAULT);
        } else {
            return Base64.getUrlDecoder().decode(data);
        }
    }

}

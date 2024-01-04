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

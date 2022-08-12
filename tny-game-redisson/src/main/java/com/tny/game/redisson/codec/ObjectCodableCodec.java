/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.redisson.codec;

import com.tny.game.codec.*;
import io.netty.buffer.*;
import io.netty.util.ReferenceCountUtil;
import org.apache.commons.lang3.ClassUtils;
import org.redisson.client.codec.Codec;
import org.redisson.client.protocol.*;
import org.springframework.util.MimeTypeUtils;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2020/10/12 8:19 下午
 */
public class ObjectCodableCodec extends TypedBaseCodec {

    private final ObjectCodec<Object> codec;

    public ObjectCodableCodec(Type type, ObjectCodec<Object> codec) {
        super(type, null, false);
        this.codec = codec;
    }

    public ObjectCodableCodec(Type type, String mimeType, ObjectCodecService objectCodecService) {
        this(type, mimeType, objectCodecService, null);
    }

    public ObjectCodableCodec(Type type, String mimeType, ObjectCodecService objectCodecService, Codec keyCodec) {
        super(type, keyCodec, false);
        this.codec = objectCodecService.codec(type, mimeType);
        this.initCodec();
    }

    @Override
    protected Decoder<Object> createDecoder(Type type) {
        return (buf, state) -> this.codec.decode(ByteBufUtil.getBytes(buf));
    }

    @Override
    protected Encoder createEncoder(Type type) {
        return in -> {
            ByteBuf out = ByteBufAllocator.DEFAULT.buffer();
            try (ByteBufOutputStream os = new ByteBufOutputStream(out)) {
                if (in instanceof byte[]) {
                    os.write((byte[])in);
                } else if (ClassUtils.isPrimitiveOrWrapper(in.getClass()) || in instanceof CharSequence) {
                    os.write(in.toString().getBytes(CoderCharsets.DEFAULT));
                } else {
                    os.write(this.codec.encode(in));
                }
                return os.buffer();
            } catch (IOException e) {
                ReferenceCountUtil.release(out);
                throw e;
            }
        };
    }

    public static void main(String[] args) {
        System.out.println(MimeTypeUtils.parseMimeType("json"));
    }

}

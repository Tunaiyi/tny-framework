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

import org.springframework.util.MimeType;

import java.lang.reflect.Type;
import java.util.Collection;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2020/8/19 6:24 下午
 */
public interface ObjectCodecFactory {

    Collection<MimeType> getMediaTypes();

    <T> ObjectCodec<T> createCodec(Type clazz);

    MimeType isCanCodec(Class<?> clazz);

}

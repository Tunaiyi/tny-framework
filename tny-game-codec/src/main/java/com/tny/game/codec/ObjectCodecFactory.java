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

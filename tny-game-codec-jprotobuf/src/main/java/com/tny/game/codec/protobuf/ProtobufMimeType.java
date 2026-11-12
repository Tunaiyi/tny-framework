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

package com.tny.game.codec.protobuf;

import com.tny.game.codec.*;
import org.springframework.util.MimeType;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/23 7:41 下午
 */
public interface ProtobufMimeType {

    MimeType PROTOBUF_MIME_TYPE = MimeType.valueOf(ProtobufMimeType.PROTOBUF);

    String PROTOBUF = "application/protobuf";

    String PROTOBUF_SUB_TYPE = "protobuf";

    String PROTOBUF_WILDCARD = MimeTypeAide.wildcardType(PROTOBUF_SUB_TYPE);

}

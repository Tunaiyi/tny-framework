/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.codec.typeprotobuf;

import com.tny.game.codec.*;
import org.springframework.util.MimeType;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/23 7:41 下午
 */
public interface TypeProtobufMimeType {

    MimeType TYPE_PROTOBUF_MIME_TYPE = MimeType.valueOf(TypeProtobufMimeType.TYPE_PROTOBUF);

    String TYPE_PROTOBUF = "application/type-protobuf";

    String TYPE_PROTOBUF_SUB_TYPE = "type-protobuf";

    String TYPE_PROTOBUF_WILDCARD = MimeTypeAide.wildcardType(TYPE_PROTOBUF_SUB_TYPE);

}

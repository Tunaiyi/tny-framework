package com.tny.game.common.codec.typeprotobuf;

import com.tny.game.common.codec.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/23 7:41 下午
 */
public interface TypeProtobufMimeType {

    String TYPE_PROTOBUF = "application/type-protobuf";

    String TYPE_PROTOBUF_SUB_TYPE = "type-protobuf";

    String TYPE_PROTOBUF_WILDCARD = MimeTypeAide.wildcardType(TYPE_PROTOBUF_SUB_TYPE);

}

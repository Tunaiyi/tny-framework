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

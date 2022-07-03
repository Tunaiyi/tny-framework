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

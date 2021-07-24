package com.tny.game.common.codec.protobuf;

import com.tny.game.common.codec.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/23 7:41 下午
 */
public interface ProtobufMimeType {

    String PROTOBUF = "application/protobuf";

    String PROTOBUF_SUB_TYPE = "protobuf";

    String PROTOBUF_WILDCARD = MimeTypeAide.wildcardType(PROTOBUF_SUB_TYPE);

}

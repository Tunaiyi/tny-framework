package com.tny.game.codec.jackson;

import com.tny.game.codec.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/23 7:41 下午
 */
public interface JsonMimeType {

    String JSON = "application/json";

    String JSON_SUB_TYPE = "json";

    String JSON_WILDCARD = MimeTypeAide.wildcardType(JSON_SUB_TYPE);

}

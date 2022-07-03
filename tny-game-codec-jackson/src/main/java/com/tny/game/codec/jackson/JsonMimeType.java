package com.tny.game.codec.jackson;

import com.tny.game.codec.*;
import org.springframework.util.MimeType;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/23 7:41 下午
 */
public interface JsonMimeType {

    MimeType JSON_MIME_TYPE = MimeType.valueOf(JsonMimeType.JSON);

    String JSON = "application/json";

    String JSON_SUB_TYPE = "json";

    String JSON_WILDCARD = MimeTypeAide.wildcardType(JSON_SUB_TYPE);

}

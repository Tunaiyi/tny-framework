package com.tny.game.codec.protobuf;

import com.tny.game.codec.*;
import com.tny.game.codec.annotation.*;
import com.tny.game.common.utils.*;
import com.tny.game.scanner.*;
import com.tny.game.scanner.annotation.*;
import com.tny.game.scanner.filter.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.*;
import org.springframework.util.MimeType;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2020-03-05 13:08
 */
public class ProtobufObjectLoader {

    public static final Logger LOGGER = LoggerFactory.getLogger(ProtobufObjectLoader.class);

    private static final ProtobufObjectCodecFactory FACTORY = new ProtobufObjectCodecFactory();

    @ClassSelectorProvider
    static ClassSelector autoMixClassesSelector() {
        return ClassSelector.instance()
                .addFilter(AnnotationClassFilter.ofInclude(Codecable.class))
                .setHandler((classes) -> {
                    MimeType findType = MimeType.valueOf(ProtobufMimeType.PROTOBUF_WILDCARD);
                    classes.forEach(cl -> {
                        Codecable codecable = cl.getAnnotation(Codecable.class);
                        String mime = MimeTypeAide.getMimeType(codecable);
                        Asserts.checkArgument(StringUtils.isNoneBlank(mime), "{} mime {} is blank", cl, mime);
                        MimeType mimeType = MimeType.valueOf(mime);
                        if (findType.isCompatibleWith(mimeType)) {
                            try {
                                FACTORY.createCodec(cl);
                            } catch (Throwable e) {
                                LOGGER.error("{} create codecor exception", cl, e);
                                throw e;
                            }
                        }
                    });
                });
    }

}

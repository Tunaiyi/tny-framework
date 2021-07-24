package com.tny.game.common.codec.protobuf;

import com.tny.game.common.codec.*;
import com.tny.game.common.codec.annotation.*;
import com.tny.game.common.utils.*;
import com.tny.game.scanner.*;
import com.tny.game.scanner.annotation.*;
import com.tny.game.scanner.filter.*;
import org.springframework.util.MimeType;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2020-03-05 13:08
 */
public class ProtobufObjectLoader {

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
                        Asserts.checkNotNull(mime, "{} mime is null", cl);
                        MimeType mimeType = MimeType.valueOf(mime);
                        if (findType.isCompatibleWith(mimeType)) {
                            FACTORY.createCodecor(cl);
                        }
                    });
                });
    }

}

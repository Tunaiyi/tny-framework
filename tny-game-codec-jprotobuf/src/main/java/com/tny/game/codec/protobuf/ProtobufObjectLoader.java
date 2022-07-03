package com.tny.game.codec.protobuf;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import com.tny.game.scanner.*;
import com.tny.game.scanner.annotation.*;
import com.tny.game.scanner.filter.*;
import org.slf4j.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2020-03-05 13:08
 */
public class ProtobufObjectLoader {

    public static final Logger LOGGER = LoggerFactory.getLogger(ProtobufObjectLoader.class);

    @ClassSelectorProvider
    static ClassSelector autoProtobufSelector() {
        return ClassSelector.create()
                .addFilter(AnnotationClassFilter.ofInclude(ProtobufClass.class))
                .setHandler((classes) -> {
                    ProtobufObjectCodecFactory factory = ProtobufObjectCodecFactory.getInstance();
                    classes.forEach(cl -> {
                        try {
                            factory.createCodec(cl);
                        } catch (Throwable e) {
                            LOGGER.error("{} create codecor exception", cl, e);
                            throw e;
                        }
                    });
                });
    }

}

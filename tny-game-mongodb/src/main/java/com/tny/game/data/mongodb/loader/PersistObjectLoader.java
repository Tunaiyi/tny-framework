package com.tny.game.data.mongodb.loader;

import com.tny.game.common.concurrent.collection.*;
import com.tny.game.scanner.*;
import com.tny.game.scanner.annotation.*;
import com.tny.game.scanner.filter.*;
import org.slf4j.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

import static com.tny.game.codec.jackson.mapper.AutoRegisterModuleClassesHandler.*;

/**
 * <p>
 */
public final class PersistObjectLoader {

    public static final Logger LOGGER = LoggerFactory.getLogger(PersistObjectLoader.class);

    private static final Set<Class<?>> CONVERTER_CLASSES = new ConcurrentHashSet<>();

    private PersistObjectLoader() {
    }

    @ClassSelectorProvider
    static ClassSelector mixDocumentSelector() {
        return ClassSelector.create()
                .addFilter(AnnotationClassFilter.ofInclude(Document.class))
                .setHandler(createHandler((module, classes) ->
                        //							module.setMixInAnnotation(docClass, MongoIdMix.class);
                        CONVERTER_CLASSES.addAll(classes)));
    }

    public static Set<Class<?>> getConverterClasses() {
        return Collections.unmodifiableSet(CONVERTER_CLASSES);
    }

}

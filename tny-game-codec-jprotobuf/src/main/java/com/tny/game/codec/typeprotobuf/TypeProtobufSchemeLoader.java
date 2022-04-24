package com.tny.game.codec.typeprotobuf;

import com.tny.game.codec.typeprotobuf.annotation.*;
import com.tny.game.scanner.*;
import com.tny.game.scanner.annotation.*;
import com.tny.game.scanner.filter.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2020-03-05 13:08
 */
public class TypeProtobufSchemeLoader {

    @ClassSelectorProvider
    static ClassSelector autoMixClassesSelector() {
        return ClassSelector.create()
                .addFilter(AnnotationClassFilter.ofInclude(TypeProtobuf.class))
                .setHandler((classes) -> classes.forEach(cl -> TypeProtobufSchemeManager.getInstance().loadScheme(cl)));
    }

}

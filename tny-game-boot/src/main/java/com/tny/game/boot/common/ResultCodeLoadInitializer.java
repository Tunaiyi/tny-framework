package com.tny.game.boot.common;

import com.tny.game.common.lifecycle.annotation.*;
import com.tny.game.common.result.*;
import com.tny.game.common.type.*;
import com.tny.game.scanner.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/11 1:12 下午
 */
@AsLifecycle
public class ResultCodeLoadInitializer {

    @StaticInit
    static <E extends Enum<E> & ResultCode> void loadClass() {
        ReferenceType<Class<E>> type = new ReferenceType<Class<E>>() {

        };
        AutoLoadClasses.getClasses(ResultCode.class)
                .stream()
                .map(c -> as(c, type))
                .forEach(ResultCodes::registerClass);
    }

}

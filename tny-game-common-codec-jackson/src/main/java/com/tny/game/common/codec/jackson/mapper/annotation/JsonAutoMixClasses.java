package com.tny.game.common.codec.jackson.mapper.annotation;

import java.lang.annotation.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2019-11-01 14:19
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonAutoMixClasses {

    Class<?>[] value();

}

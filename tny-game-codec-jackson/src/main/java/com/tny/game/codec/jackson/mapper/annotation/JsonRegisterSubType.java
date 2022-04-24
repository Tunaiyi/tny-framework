package com.tny.game.codec.jackson.mapper.annotation;

import java.lang.annotation.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/20 3:31 下午
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonRegisterSubType {

    String value();

    String prefix() default "";

    String link() default "-";

}

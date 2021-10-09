package com.tny.game.data.annotation;

import java.lang.annotation.*;

/**
 * <p>
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface EntityKey {

}
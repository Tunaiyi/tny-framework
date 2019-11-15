package com.tny.game.net.annotation;

import java.lang.annotation.*;

/**
 * Contoller需要检测
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface AfterPlugins {

    AfterPlugin[] value();

}
package com.tny.game.net.annotation;

import com.tny.game.net.checker.ControllerChecker;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Contoller需要检测
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Repeatable(Checkers.class)
@Documented
public @interface Check {

    /**
     * @return 检测顺序
     */
    Class<? extends ControllerChecker> value();

    /**
     * @return 参数, 使用mvel公式返回
     */
    String attributesFx() default "";

    /**
     * @return 参数
     */
    String attributes() default "";

}
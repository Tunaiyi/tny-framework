package com.tny.game.common.lifecycle.unit.annotation;

import java.lang.annotation.*;

/**
 * 标识组件
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Unit {

    /**
     * @return 组件名字
     */
    String value() default "";

    /**
     * @return 注册 Unit 接口, 这些Class必须是 Class 继承或实现的
     */
    Class<?>[] unitInterfaces() default {};

}
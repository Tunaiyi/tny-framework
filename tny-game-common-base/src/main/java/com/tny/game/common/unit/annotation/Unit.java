package com.tny.game.common.unit.annotation;

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
     * 注册 Unit 接口, 这些Class必须是 Class 继承或实现的
     *
     * @return
     */
    Class<?>[] unitInterfaces() default {};

}
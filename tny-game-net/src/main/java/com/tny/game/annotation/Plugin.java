package com.tny.game.annotation;

import com.tny.game.net.plugin.ControllerPlugin;

import java.lang.annotation.*;

/**
 * @author KGTny
 * @ClassName: Plugin
 * @Description: 控制器插件註解
 * @date 2011-9-22 下午5:00:55
 * <p>
 * 控制器插件註解
 * <p>
 * 被標記的控制器會在調用方法的前後調用所標識的插件, METHOD的優先級高於TYPE<br>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Plugin {

    /**
     * 插件类型数组
     * <p>
     * <p>
     * 在调用Controller的方法后调用execute;<br>
     *
     * @return
     */
    Class<? extends ControllerPlugin>[] after() default {};

    /**
     * 插件类型数组
     * <p>
     * <p>
     * 在调用Controller的方法后调用execute;<br>
     *
     * @return
     */
    Class<? extends ControllerPlugin>[] before() default {};
}

package com.tny.game.net.annotation;

import com.tny.game.net.command.ControllerPlugin;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
@Repeatable(BeforePlugins.class)
@Documented
public @interface BeforePlugin {

    /**
     * 插件类型数组
     * <p>
     * <p>
     * 在调用Controller的方法后调用execute;<br>
     *
     * @return
     */
    Class<? extends ControllerPlugin> value();

}
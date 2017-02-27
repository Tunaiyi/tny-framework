package com.tny.game.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
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
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface MsgCode {

}
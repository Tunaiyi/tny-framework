package com.tny.game.basics.item.annotation;

import java.lang.annotation.*;

/**
 * 单独的 entity, 加入次注解的对象可以通过 playerId 直接找到, 无需 id
 * Created by Kun Yang on 2018/1/4.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface SingleEntity {

    boolean value() default true;

}

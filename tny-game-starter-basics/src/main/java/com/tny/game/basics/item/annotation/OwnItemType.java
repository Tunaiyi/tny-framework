package com.tny.game.basics.item.annotation;

import java.lang.annotation.*;

/**
 * Created by Kun Yang on 2018/1/4.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface OwnItemType {

    int[] value();

}

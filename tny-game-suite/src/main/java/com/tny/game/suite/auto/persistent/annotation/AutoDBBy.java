package com.tny.game.suite.auto.persistent.annotation;

import com.tny.game.basics.item.*;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface AutoDBBy {

    Class<? extends Manager<?>> manager();

}

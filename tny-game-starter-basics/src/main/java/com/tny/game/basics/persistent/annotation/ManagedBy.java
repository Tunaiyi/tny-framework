package com.tny.game.basics.persistent.annotation;

import com.tny.game.basics.item.*;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ManagedBy {

    Class<? extends Manager<?>> manager();

}

package com.tny.game.oplog.annotation;

import com.tny.game.oplog.Snapper;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Snapshot {

    Class<? extends Snapper>[] value() default {};

}

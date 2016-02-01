package com.tny.game.oplog.annotation;

import com.tny.game.oplog.Snapper;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface SnapBy {

    Class<? extends Snapper> value();

}

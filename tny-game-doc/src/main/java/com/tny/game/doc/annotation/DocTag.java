package com.tny.game.doc.annotation;

import java.lang.annotation.*;

/**
 * Created by Kun Yang on 2017/6/8.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DocTag {

    String[] value();

}

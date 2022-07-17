package com.tny.game.doc.annotation;

import java.lang.annotation.*;

/**
 * Created by Kun Yang on 2017/6/8.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(DocTags.class)
@Documented
public @interface DocTag {

    /**
     * @return tag 值
     */
    String tag();

    /**
     * @return tag
     */
    String value() default "";

}

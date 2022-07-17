package com.tny.game.doc.annotation;

import java.lang.annotation.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/7/17 02:41
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DocTags {

    DocTag[] value();

}

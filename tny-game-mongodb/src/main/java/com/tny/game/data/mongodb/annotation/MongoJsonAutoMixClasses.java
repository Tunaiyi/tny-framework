package com.tny.game.data.mongodb.annotation;

import java.lang.annotation.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2019-11-01 14:19
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MongoJsonAutoMixClasses {

	Class<?>[] value();

}

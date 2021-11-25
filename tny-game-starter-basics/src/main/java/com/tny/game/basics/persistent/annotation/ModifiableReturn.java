package com.tny.game.basics.persistent.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ModifiableReturn {

	boolean immediately() default false;

	Modify modify() default Modify.SAVE;

}

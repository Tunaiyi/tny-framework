package com.tny.game.basics.persistent.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ModifiableParam {

	/**
	 * @return 立即执行
	 */
	boolean immediately() default false;

	/**
	 * @return 操作
	 */
	Modify modify() default Modify.SAVE;

}

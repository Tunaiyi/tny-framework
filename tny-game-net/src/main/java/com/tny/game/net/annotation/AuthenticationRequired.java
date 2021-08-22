package com.tny.game.net.annotation;

import com.tny.game.net.command.auth.*;

import java.lang.annotation.*;

/**
 * Contoller需要认证
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface AuthenticationRequired {

	/**
	 * 用户组名称
	 * <p>
	 * <p>
	 * 当userType System<br>
	 */
	String[] value() default {};

	/**
	 * @return 验证器
	 */
	Class<? extends AuthenticateValidator> validator() default AuthenticateValidator.class;

	boolean enable() default true;

}
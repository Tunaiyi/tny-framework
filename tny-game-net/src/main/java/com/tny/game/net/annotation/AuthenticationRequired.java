package com.tny.game.net.annotation;

import com.tny.game.net.command.auth.AuthenticateValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
     *
     * @return
     */
    String[] value() default {};

    /**
     * @return 验证器
     */
    Class<? extends AuthenticateValidator> validator() default AuthenticateValidator.class;

    boolean enable() default true;

}
package com.tny.game.net.annotation;

import java.lang.annotation.*;

/**
 * 配置Contorller生效应用类型
 * Created by Kun Yang on 2017/3/24.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ScopeProfile {

    String[] value();

}

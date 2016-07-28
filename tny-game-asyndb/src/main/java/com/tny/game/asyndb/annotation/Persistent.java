package com.tny.game.asyndb.annotation;

import com.tny.game.asyndb.Synchronizer;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 持久化注解
 *
 * @author KGTny
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Persistent {

    /**
     * @return 是否是异步持久化，默认是true
     */
    boolean asyn() default true;

    /**
     * @return 获取数据库同步器类型
     */
    Class<? extends Synchronizer> synchronizerClass();

    /**
     * @return 每次访问生命周期
     */
    long lifeTime() default Long.MIN_VALUE;

}

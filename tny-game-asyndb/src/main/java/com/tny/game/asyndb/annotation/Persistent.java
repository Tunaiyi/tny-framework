package com.tny.game.asyndb.annotation;

import com.tny.game.asyndb.Synchronizer;

import java.lang.annotation.*;

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
     * 是否是异步持久化，默认是true
     *
     * @return
     */
    boolean asyn() default true;

    /**
     * 获取数据库同步器类型
     *
     * @return
     */
    @SuppressWarnings("rawtypes") Class<? extends Synchronizer> synchronizerClass();

}

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
    public boolean asyn() default true;

    /**
     * 如果更新的对象不是同一个时是否替换
     * replaceObject = true :
     * 更新对象直接替换内存对象
     * replaceObject = false
     * 更新对象不是内存对象则刨出异常
     *
     * @return
     */
    public boolean replaceObject() default false;

    /**
     * 获取数据库同步器类型
     *
     * @return
     */
    @SuppressWarnings("rawtypes")
    public Class<? extends Synchronizer> synchronizerClass();

}

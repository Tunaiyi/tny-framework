package com.tny.game.asyndb.annotation;

import java.lang.annotation.*;

/**
 * 强制替换注解
 * 如果更新的对象不是同一个时是否替换
 * replaceObject = true :
 * 更新对象直接替换内存对象
 * replaceObject = false
 * 更新对象不是内存对象则刨出异常
 *
 * @author KGTny
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Replace {

}

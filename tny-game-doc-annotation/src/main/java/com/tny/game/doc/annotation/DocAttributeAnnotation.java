package com.tny.game.doc.annotation;

import java.lang.annotation.*;

/**
 * 用来标识 DocAnnotationClass 注解哪个方法代表 id
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/5 9:24 下午
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface DocAttributeAnnotation {

}

package com.tny.game.doc.annotation;

import java.lang.annotation.*;

@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FunDoc {

	String des();

	String text() default "";

	Class<?> returnType() default Object.class;

	String returnDes() default "";

}

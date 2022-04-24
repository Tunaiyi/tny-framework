package com.tny.game.doc.annotation;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FunDoc {

    String des();

    String text() default "";

    Class<?> returnType() default Void.class;

    String returnDes() default "";

}

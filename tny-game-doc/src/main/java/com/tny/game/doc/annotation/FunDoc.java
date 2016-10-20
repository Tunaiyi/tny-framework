package com.tny.game.doc.annotation;

import com.thoughtworks.xstream.mapper.Mapper.Null;

import java.lang.annotation.*;

@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FunDoc {

    String des();

    String text() default "";

    Class<?> returnType() default Null.class;

    String returnDes() default "";

}

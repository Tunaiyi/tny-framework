package com.tny.game.doc.annotation;

import com.thoughtworks.xstream.mapper.Mapper.Null;

import java.lang.annotation.*;

@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FunDoc {

    public String des();

    public Class<?> returnType() default Null.class;

    public String returnDes() default "";

}

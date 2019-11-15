package com.tny.game.web.converter.excel.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ExcelColumn {

    int index();

    String columnText();

    String name() default "";

    int width() default 20;

}

package com.tny.game.net.command.filter.range.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DoubleRange {

    public double low();

    public double high();

}

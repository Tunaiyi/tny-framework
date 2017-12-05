package com.tny.game.net.command.filter.string.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PatternMatch {

    public String pattern();

}

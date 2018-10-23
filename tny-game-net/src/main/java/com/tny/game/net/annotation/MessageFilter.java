package com.tny.game.net.annotation;

import com.tny.game.net.message.MessageMode;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 消息处理限制, 只有配置的消息码才会处理
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface MessageFilter {

    MessageMode[] modes() default {
            MessageMode.PUSH,
            MessageMode.REQUEST,
            MessageMode.PUSH
    };

}
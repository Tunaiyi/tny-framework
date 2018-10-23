package com.tny.game.net.message.annotation;

import java.lang.annotation.*;

/**
 * 消息分线标签
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-16 11:06
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface MessageLine {

    short value();

}

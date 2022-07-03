package com.tny.game.boot.event.annotation;

import com.tny.game.common.event.bus.annotation.*;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author KGTny
 * @ClassName: Listener
 * @Description: 监听器配置注解
 * @date 2011-9-21 ????11:55:26
 * <p>
 * 监听器配置注解
 * <p>
 * 将监听器绑定到与dispatcherClasses对应的Dispatcher<br>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Component
@Documented
public @interface EventHandler {

    GlobalEventListener listener() default @GlobalEventListener;

}

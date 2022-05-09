package com.tny.game.net.annotation;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/4 8:05 下午
 */

import com.tny.game.net.message.*;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RpcController {

    /**
     * @return 默认处理所有
     */
    MessageMode[] modes() default {};

}
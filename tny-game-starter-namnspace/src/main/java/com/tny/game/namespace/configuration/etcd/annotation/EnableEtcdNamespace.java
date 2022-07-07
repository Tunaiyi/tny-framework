package com.tny.game.namespace.configuration.etcd.annotation;

import com.tny.game.namespace.configuration.*;
import com.tny.game.namespace.configuration.etcd.*;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/7/4 01:53
 **/

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({
        EtcdNamespaceAutoConfiguration.class,
        NamespaceAutoConfiguration.class
})
public @interface EnableEtcdNamespace {

}

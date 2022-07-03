package com.tny.game.namespace.configuration.etcd;

import com.tny.game.namespace.etcd.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/7/4 01:47
 **/

@ConfigurationProperties(prefix = "tny.namespace.etcd")
public class EtcdNamespaceProperties extends EtcdConfig {

}

package com.tny.game.suite.net.configuration;

import com.tny.game.net.base.NetUnitSetting;
import com.tny.game.net.netty4.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-11-03 10:59
 */
public interface NetConfigurationAide {


    String HEAD_KEY = "tny";

    String APP_KEY = key(HEAD_KEY, "app");
    String APP_CONTEXT_BEAN_NAME = "appContext";

    String NET_HEAD = key(HEAD_KEY, "net");
    String NET_SETTING_NODE = "setting";
    String NET_NAMES_NODE = "names";

    String SERVER_HEAD = key(NET_HEAD, "server");
    String SERVER_NAMES_KEY = key(SERVER_HEAD, NET_NAMES_NODE);

    String CLIENT_HEAD = key(NET_HEAD, "client");
    String CLIENT_NAMES_KEY = key(CLIENT_HEAD, NET_NAMES_NODE);

    String HANDLER_HEAD = key(NET_HEAD, "handler");
    String EXECUTOR_HEAD = key(NET_HEAD, "executor");

    String SESSION_HEAD = key(NET_HEAD, "session");
    String FACTORY_KEY = "keeper_factory";

    enum NetType {

        SERVER(SERVER_HEAD, NettyServerUnitSetting::new),

        CLIENT(CLIENT_HEAD, NettyClientUnitSetting::new),

        //
        ;

        private String head;

        private Function<String, NetUnitSetting> settingCreator;

        NetType(String head, Function<String, NetUnitSetting> settingCreator) {
            this.head = head;
            this.settingCreator = settingCreator;
        }

        public NetUnitSetting createSetting(String name) {
            return settingCreator.apply(name);
        }

        String getHead() {
            return head;
        }
    }

    static String beanName(Class<?> clazz) {
        String name = clazz.getSimpleName();
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }

    static Set<String> getNames(Environment environment, String keyHead) {
        int nameIndex = StringUtils.split(keyHead, ".").length;
        List<String> propertiesAppNames = as(environment.getProperty(keyHead, List.class));
        if (propertiesAppNames != null) {
            return new HashSet<>(propertiesAppNames);
        } else {
            MutablePropertySources propertySources = ((ConfigurableEnvironment) environment).getPropertySources();
            return StreamSupport.stream(propertySources.spliterator(), true)
                    .filter(ps -> ps instanceof EnumerablePropertySource)
                    .map(ps -> ((EnumerablePropertySource) ps).getPropertyNames())
                    .flatMap(Arrays::stream)
                    .filter(propName -> propName.startsWith(keyHead + "."))
                    .filter(propName -> !Objects.equals(propName, keyHead))
                    .map(propName -> StringUtils.split(propName, "."))
                    .filter(propNameWords -> propNameWords.length >= nameIndex)
                    .map(propNameWords -> propNameWords[nameIndex])
                    .collect(Collectors.toSet());
        }
    }

    static String key(NetType net, String... nodes) {
        StringBuilder builder = new StringBuilder()
                .append(net.getHead());
        for (String node : nodes) {
            builder.append(".").append(node);
        }
        return builder.toString();
    }

    static String key(String... nodes) {
        StringBuilder builder = new StringBuilder();
        for (String node : nodes) {
            if (builder.length() == 0)
                builder.append(node);
            else
                builder.append(".").append(node);
        }
        return builder.toString();
    }
}

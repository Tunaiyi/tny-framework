package com.tny.game.suite.net.configuration;

import com.tny.game.net.base.*;
import com.tny.game.net.netty4.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 */
public interface NetConfigurationAide {

    String DEFAULT_NAME_KEY = "default";

    String HEAD_KEY = "tny";

    String APP_KEY = key(HEAD_KEY, "app");
    String APP_CONTEXT_BEAN_NAME = "appContext";

    String NET_HEAD = key(HEAD_KEY, "net");
    String NET_CHANNEL_NODE = "channel";
    String NET_ENCODER_NODE = "encoder";
    String NET_DECODER_NODE = "decoder";
    String CLASS_NODE = "class";
    String SETTING_CLASS_NODE = "setting-class";
    String NET_NAMES_NODE = "names";

    String SERVER_HEAD = key(NET_HEAD, "server");

    String CLIENT_HEAD = key(NET_HEAD, "client");

    String EVENT_HANDLER_HEAD = key(NET_HEAD, "event-handler");

    // String MESSAGE_HANDLER_HEAD = key(NET_HEAD, "message-handler");

    String COMMAND_EXECUTOR_HEAD = key(NET_HEAD, "command-executor");

    String COMMAND_DISPATCHER_HEAD = key(NET_HEAD, "command-dispatcher");

    String SESSION_HEAD = key(NET_HEAD, "session");
    String TERMINAL_HEAD = key(NET_HEAD, "terminal");

    String SESSION_KEEPER_HEAD = key(NET_HEAD, "session-keeper");
    String TERMINAL_KEEPER_HEAD = key(NET_HEAD, "terminal-keeper");

    String SESSION_FACTORY_HEAD = key(NET_HEAD, "session-factory");

    static String getBeanName(String beanName, Class<?> unitInterface) {
        if (beanName.equals(DEFAULT_NAME_KEY)) {
            return DEFAULT_NAME_KEY + unitInterface.getSimpleName();
        } else {
            return beanName;
        }
    }

    enum NetType {

        SERVER(SERVER_HEAD, NettyServerBootstrapSetting::new),

        CLIENT(CLIENT_HEAD, NettyClientBootstrapSetting::new),

        //
        ;

        private String head;

        private Function<String, NetBootstrapSetting> settingCreator;

        NetType(String head, Function<String, NetBootstrapSetting> settingCreator) {
            this.head = head;
            this.settingCreator = settingCreator;
        }

        public NetBootstrapSetting createSetting(String name) {
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
                                .filter(EnumerablePropertySource.class::isInstance)
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

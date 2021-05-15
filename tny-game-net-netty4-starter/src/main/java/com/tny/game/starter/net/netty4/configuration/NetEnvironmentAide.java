package com.tny.game.starter.net.netty4.configuration;

import com.tny.game.net.base.*;
import com.tny.game.net.netty4.*;

import java.util.function.Function;

import static com.tny.game.starter.common.environment.EnvironmentAide.*;

/**
 * <p>
 */
public interface NetEnvironmentAide {

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

    String FILTER_HEAD = key(NET_HEAD, "filter");

    enum NetType {

        SERVER(SERVER_HEAD, NettyServerBootstrapSetting::new),

        CLIENT(CLIENT_HEAD, NettyClientBootstrapSetting::new),

        //
        ;

        private final String head;

        private final Function<String, NetBootstrapSetting> settingCreator;

        NetType(String head, Function<String, NetBootstrapSetting> settingCreator) {
            this.head = head;
            this.settingCreator = settingCreator;
        }

        public NetBootstrapSetting createSetting(String name) {
            return this.settingCreator.apply(name);
        }

        String getHead() {
            return this.head;
        }
    }

    static String keyOf(NetType net, String... nodes) {
        StringBuilder builder = new StringBuilder()
                .append(net.getHead());
        for (String node : nodes) {
            builder.append(".").append(node);
        }
        return builder.toString();
    }

}

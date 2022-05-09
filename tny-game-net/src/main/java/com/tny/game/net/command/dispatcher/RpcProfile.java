package com.tny.game.net.command.dispatcher;

import com.tny.game.net.annotation.*;
import com.tny.game.net.message.*;
import org.slf4j.*;

import java.lang.reflect.Method;
import java.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/4 8:54 下午
 */
public class RpcProfile {

    public static final Logger LOGGER = LoggerFactory.getLogger(RpcProfile.class);

    private final int protocol;

    private final int line;

    private final MessageMode mode;

    private RpcProfile(int protocol, int line, MessageMode mode) {
        this.protocol = protocol;
        this.line = line;
        this.mode = mode;
    }

    public Class<?> getRouter() {
        return null;
    }

    public static RpcProfile oneOf(Method method) {
        RpcRequest rpcRequest = method.getAnnotation(RpcRequest.class);
        if (rpcRequest != null) {
            return new RpcProfile(rpcRequest.value(), rpcRequest.line(), MessageMode.REQUEST);
        }
        RpcPush rpcPush = method.getAnnotation(RpcPush.class);
        if (rpcPush != null) {
            return new RpcProfile(rpcPush.value(), rpcPush.line(), MessageMode.PUSH);
        }
        RpcResponse rpcResponse = method.getAnnotation(RpcResponse.class);
        if (rpcResponse != null) {
            return new RpcProfile(rpcResponse.value(), rpcResponse.line(), MessageMode.RESPONSE);
        }
        LOGGER.warn("{} 没有存在注解 {}, {}, {} 中的一个", method, RpcRequest.class, RpcPush.class, RpcResponse.class);
        return null;
    }

    public static List<RpcProfile> allOf(Method method, MessageMode... defaultModes) {
        List<RpcProfile> profiles = new ArrayList<>();
        RpcRequest rpcRequest = method.getAnnotation(RpcRequest.class);
        if (rpcRequest != null) {
            profiles.add(new RpcProfile(rpcRequest.value(), rpcRequest.line(), MessageMode.REQUEST));
        }
        RpcPush rpcPush = method.getAnnotation(RpcPush.class);
        if (rpcPush != null) {
            profiles.add(new RpcProfile(rpcPush.value(), rpcPush.line(), MessageMode.PUSH));
        }
        RpcResponse rpcResponse = method.getAnnotation(RpcResponse.class);
        if (rpcResponse != null) {
            profiles.add(new RpcProfile(rpcResponse.value(), rpcResponse.line(), MessageMode.RESPONSE));
        }
        Rpc rpc = method.getAnnotation(Rpc.class);
        if (rpc != null) {
            if (rpc.modes().length > 0) {
                for (MessageMode mode : rpc.modes()) {
                    profiles.add(new RpcProfile(rpc.value(), rpc.line(), mode));
                }
            } else {
                for (MessageMode mode : defaultModes) {
                    profiles.add(new RpcProfile(rpc.value(), rpc.line(), mode));
                }
            }
        }
        if (profiles.isEmpty()) {
            LOGGER.warn("{} 没有存在注解 {}, {}, {}, {} 中的一个", method, RpcRequest.class, RpcPush.class, RpcResponse.class, Rpc.class);
        }
        return profiles;
    }

    public int getProtocol() {
        return protocol;
    }

    public int getLine() {
        return line;
    }

    public MessageMode getMode() {
        return mode;
    }

}

package com.tny.game.net.base;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/28 05:11
 **/
public interface RpcServiceType extends MessagerType {

    String getService();

    @Override
    default String getGroup() {
        return getService();
    }

    default void register() {
        RpcServiceTypes.register(this);
    }

}

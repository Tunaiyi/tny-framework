package com.tny.game.net.rpc;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/29 3:41 上午
 */
public interface Servicer<S, U> {

    S to(U forUser);

}

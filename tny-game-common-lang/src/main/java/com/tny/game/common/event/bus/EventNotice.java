package com.tny.game.common.event.bus;

/**
 * <p>
 *
 * @author kgtny
 * @date 2024/1/5 13:46
 **/
public interface EventNotice<S> {

    S getSource();

}

package com.tny.game.common.event;


/**
 * Created by Kun Yang on 16/2/4.
 */
public interface TestListener {

    default void handleCreate(String string) {
    }
    ;

    default void handleUpgrade(String string, Integer level) {
    }
    ;

}

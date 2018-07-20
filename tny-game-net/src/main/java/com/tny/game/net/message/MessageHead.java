package com.tny.game.net.message;

/**
 * Created by Kun Yang on 2018/7/17.
 */
public interface MessageHead {

    int getID();

    int getCode();

    long getTime();

    int getToMessage();

    int getProtocol();



}

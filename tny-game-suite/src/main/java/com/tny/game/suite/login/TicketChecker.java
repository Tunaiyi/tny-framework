package com.tny.game.suite.login;

import com.tny.game.net.dispatcher.Request;

public interface TicketChecker<T> {

    boolean check(Request request, T ticket);

}

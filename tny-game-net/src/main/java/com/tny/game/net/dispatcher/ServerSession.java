package com.tny.game.net.dispatcher;

import com.tny.game.net.checker.RequestChecker;

import java.util.List;

public interface ServerSession extends BaseSession, ResponseSession {

    List<RequestChecker> getCheckers();

}
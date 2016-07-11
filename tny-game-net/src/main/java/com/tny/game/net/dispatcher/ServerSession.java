package com.tny.game.net.dispatcher;

import com.tny.game.net.checker.RequestChecker;

import java.util.List;

public interface ServerSession extends NetSession, ResponseSession {

    List<RequestChecker> getCheckers();

}
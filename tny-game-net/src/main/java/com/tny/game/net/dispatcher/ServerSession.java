package com.tny.game.net.dispatcher;

import com.tny.game.net.checker.RequestChecker;
import com.tny.game.net.coder.DataPacketEncoder;

public interface ServerSession extends NetSession, ResponseSession {

    DataPacketEncoder getEncoder();

    RequestChecker getChecker();

}
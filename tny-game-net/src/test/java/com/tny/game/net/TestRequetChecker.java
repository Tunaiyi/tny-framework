package com.tny.game.net;

import com.tny.game.net.checker.RequestChecker;
import com.tny.game.net.dispatcher.Request;
import org.springframework.stereotype.Component;

@Component("checker")
public class TestRequetChecker implements RequestChecker {

    @Override
    public boolean match(Request request) {
        if (request == null || request.getCheckKey() == null)
            return false;
        return true;
    }

    @Override
    public String generate(Request Request) {
        return "";
    }

}

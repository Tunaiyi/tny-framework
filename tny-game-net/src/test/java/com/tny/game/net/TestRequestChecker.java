package com.tny.game.net;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.base.CoreResponseCode;
import com.tny.game.net.checker.RequestVerifyChecker;
import com.tny.game.net.dispatcher.Request;
import org.springframework.stereotype.Component;

@Component("checker")
public class TestRequestChecker implements RequestVerifyChecker {

    @Override
    public ResultCode match(Request request) {
        if (request == null || request.getCheckKey() == null)
            return CoreResponseCode.FALSIFY;
        return ResultCode.SUCCESS;
    }

    @Override
    public String generate(Request Request) {
        return "";
    }

}

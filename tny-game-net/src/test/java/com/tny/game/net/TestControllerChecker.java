package com.tny.game.net;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.base.CoreResponseCode;
import com.tny.game.net.checker.ControllerChecker;
import com.tny.game.net.checker.MessageSignGenerator;
import com.tny.game.net.common.dispatcher.ControllerHolder;
import com.tny.game.net.message.Message;
import com.tny.game.net.tunnel.Tunnel;
import org.springframework.stereotype.Component;

@Component("checker")
public class TestControllerChecker implements MessageSignGenerator, ControllerChecker {

    @Override
    public String generate(Tunnel tunnel, Message message) {
        return "";
    }

    @Override
    public ResultCode check(Tunnel tunnel, Message message, ControllerHolder holder, Object attribute) {
        if (message == null || message.getSign() == null)
            return CoreResponseCode.FALSIFY;
        return ResultCode.SUCCESS;
    }
}

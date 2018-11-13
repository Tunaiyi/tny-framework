package com.tny.game.net.demo.server;

import com.tny.game.net.annotation.*;
import com.tny.game.net.command.plugins.BindSessionPlugin;
import com.tny.game.net.demo.common.*;
import com.tny.game.net.demo.common.dto.LoginDTO;
import com.tny.game.net.endpoint.Endpoint;
import com.tny.game.net.message.ProtocolAide;
import com.tny.game.net.transport.*;
import com.tny.game.suite.net.spring.SuiteParamFilterPlugin;

import java.time.ZonedDateTime;
import java.util.concurrent.ThreadLocalRandom;

import static com.tny.game.common.utils.StringAide.*;
import static com.tny.game.net.message.MessageMode.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-31 16:46
 */
@Controller(CtrlerIDs.LOGIN)
@AuthenticationRequired(Certificates.DEFAULT_USER_TYPE)
@BeforePlugin(SuiteParamFilterPlugin.class)
@MessageFilter(modes = {REQUEST, PUSH})
public class ServerLoginController {

    @Controller(CtrlerIDs.LOGIN$LOGIN)
    @BeforePlugin(SuiteParamFilterPlugin.class)
    @BeforePlugin(BindSessionPlugin.class)
    @AuthenticationRequired(value = Certificates.DEFAULT_USER_TYPE, validator = DemoAuthenticateValidator.class)
    public LoginDTO login(@MsgParam long sessionId, @MsgParam long userId) {
        return new LoginDTO(userId, format("{} - {} 登录成功 at {}", userId, sessionId, ZonedDateTime.now()));
    }

    @Controller(CtrlerIDs.LOGIN$SAY)
    public String say(Endpoint<Long> endpoint, @MsgParam String message) {
        endpoint.sendAsyn(MessageContexts.push(ProtocolAide.protocol(CtrlerIDs.LOGIN$PUSH), "因为 [" + message + "] 推条信息给你! " + ThreadLocalRandom.current().nextInt(3000)));
        return "respond to message : " + message;
    }
}

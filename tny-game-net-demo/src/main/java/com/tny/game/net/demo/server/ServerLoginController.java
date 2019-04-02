package com.tny.game.net.demo.server;

import com.tny.game.net.annotation.*;
import com.tny.game.net.demo.common.*;
import com.tny.game.net.demo.common.dto.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import com.tny.game.suite.net.spring.*;

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
    @AuthenticationRequired(value = Certificates.DEFAULT_USER_TYPE, validator = DemoAuthenticateValidator.class)
    public LoginDTO login(Endpoint<Long> endpoint, @MsgParam long sessionId, @MsgParam long userId) {
        Certificate<Long> certificate = endpoint.getCertificate();
        return new LoginDTO(certificate.getId(), userId, format("{} - {} 登录成功 at {}", userId, sessionId, ZonedDateTime.now()));
    }

    @Controller(CtrlerIDs.LOGIN$SAY)
    public String say(Endpoint<Long> endpoint, @MsgParam String message) {
        endpoint.send(MessageContexts.push(ProtocolAide.protocol(CtrlerIDs.LOGIN$PUSH), "因为 [" + message + "] 推条信息给你! " + ThreadLocalRandom.current().nextInt(3000)));
        return "respond to message : " + message;
    }
}

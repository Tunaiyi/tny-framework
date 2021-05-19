package com.tny.game.net.demo.client;

import com.tny.game.common.result.*;
import com.tny.game.net.annotation.*;
import com.tny.game.net.demo.common.*;
import com.tny.game.net.demo.common.dto.*;
import com.tny.game.net.transport.*;
import com.tny.game.starter.net.netty4.spring.*;
import org.slf4j.*;

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
@MessageFilter(modes = {RESPONSE, PUSH})
public class ClientLoginController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientLoginController.class);

    @Controller(CtrlerIDs.LOGIN$LOGIN)
    @BeforePlugin(SuiteParamFilterPlugin.class)
    @AuthenticationRequired(value = Certificates.DEFAULT_USER_TYPE, validator = DemoAuthenticateValidator.class)
    public void login(@MsgCode int code, @MsgBody LoginDTO dto) {
        if (!ResultCodes.isSuccess(code)) {
            LOGGER.info("Login failed : {}", code);
        } else {
            LOGGER.info("{} Login finish : {}", dto.getUserId(), dto.getMessage());
        }
    }

    @Controller(CtrlerIDs.LOGIN$PUSH)
    @BeforePlugin(SuiteParamFilterPlugin.class)
    public void pushMessage(Tunnel<Long> tunnel, @MsgBody String message) {
        //        LOGGER.info("User {} [accessId {}]receive push message {}", tunnel.getUserId(), tunnel.getAccessId(), message);
    }

    @Controller(CtrlerIDs.LOGIN$PING)
    @BeforePlugin(SuiteParamFilterPlugin.class)
    public void pingMessage(Tunnel<Long> tunnel, @MsgBody String message) {
        LOGGER.info("User {} [accessId {}] receive : {}", tunnel.getUserId(), tunnel.getAccessId(), message);
    }

}

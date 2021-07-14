package com.tny.game.net.demo.server;

import com.tny.game.common.concurrent.*;
import com.tny.game.net.annotation.*;
import com.tny.game.net.demo.common.*;
import com.tny.game.net.demo.common.dto.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import com.tny.game.starter.net.netty4.spring.*;

import java.time.ZonedDateTime;
import java.util.concurrent.*;

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
@BeforePlugin(SpringBootParamFilterPlugin.class)
@MessageFilter(modes = {REQUEST, PUSH})
public class ServerLoginController {

    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newScheduledThreadPool(1);

    @Controller(CtrlerIDs.LOGIN$LOGIN)
    @BeforePlugin(SpringBootParamFilterPlugin.class)
    @AuthenticationRequired(value = Certificates.DEFAULT_USER_TYPE, validator = DemoAuthenticateValidator.class)
    public LoginDTO login(Tunnel<Long> tunnel, Endpoint<Long> endpoint, @MsgParam long sessionId, @MsgParam long userId) {
        Certificate<Long> certificate = endpoint.getCertificate();
        //        EXECUTOR_SERVICE.scheduleAtFixedRate(() -> endpoint.send(MessageContexts
        //                .push(ProtocolAide.protocol(CtrlerIDs.LOGIN$PING), "ping tunnel id " + tunnel.getId())), 0, 3, TimeUnit.SECONDS);
        return new LoginDTO(certificate.getId(), userId, format("{} - {} 登录成功 at {}", userId, sessionId, ZonedDateTime.now()));
    }

    @Controller(CtrlerIDs.LOGIN$SAY)
    public SayContentDTO say(Endpoint<Long> endpoint, @MsgParam String message) {
        endpoint.send(MessageContexts
                .push(Protocols.protocol(CtrlerIDs.LOGIN$PUSH), "因为 [" + message + "] 推条信息给你! " + ThreadLocalRandom.current().nextInt(3000)));
        return new SayContentDTO(endpoint.getId(), "respond " + message);
    }

    @Controller(CtrlerIDs.LOGIN$TEST)
    public SayContentDTO test(Endpoint<Long> endpoint,
            @MsgParam byte byteValue,
            @MsgParam short shortValue,
            @MsgParam int intValue,
            @MsgParam long longValue,
            @MsgParam float floatValue,
            @MsgParam double doubleValue,
            @MsgParam boolean booleanValue,
            @MsgParam String message) {
        String content = "\nbyteValue:" + byteValue +
                "\nshortValue:" + shortValue +
                "\nintValue:" + intValue +
                "\nlongValue:" + longValue +
                "\nfloatValue:" + floatValue +
                "\ndoubleValue:" + doubleValue +
                "\nbooleanValue:" + booleanValue +
                "\nmessage:" + message;
        endpoint.send(MessageContexts
                .push(Protocols.protocol(CtrlerIDs.LOGIN$PUSH), content));
        return new SayContentDTO(endpoint.getId(), "test result: " + content);
    }

    @Controller(CtrlerIDs.LOGIN$DELAY_SAY)
    public Waiter<SayContentDTO> delaySay(Endpoint<Long> endpoint, @MsgParam String message, @MsgParam long delay) {
        long timeout = System.currentTimeMillis() + delay;
        return new Waiter<SayContentDTO>() {
            @Override
            public boolean isDone() {
                return System.currentTimeMillis() - timeout > 0;
            }

            @Override
            public boolean isFailed() {
                return false;
            }

            @Override
            public boolean isSuccess() {
                return isDone();
            }

            @Override
            public Throwable getCause() {
                return null;
            }

            @Override
            public SayContentDTO getResult() {
                System.out.println(message);
                return new SayContentDTO(endpoint.getId(), "delay message : " + message);
            }
        };
    }

}

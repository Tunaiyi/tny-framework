package com.tny.game.net.demo.common;

import com.tny.game.net.command.auth.AuthenticateValidator;
import com.tny.game.net.exception.CommandException;
import com.tny.game.net.message.Message;
import com.tny.game.net.transport.*;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

import static com.tny.game.common.utils.ObjectAide.as;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-31 16:44
 */
@Component
public class DemoAuthenticateValidator implements AuthenticateValidator<Long> {

    @Override
    public Certificate<Long> validate(Tunnel<Long> tunnel, Message<Long> message) throws CommandException {
        List value = message.getBody(List.class);
        return Certificates.createAutherized(as(value.get(0)), as(value.get(1)), Certificates.DEFAULT_USER_TYPE, Instant.now());
    }

}

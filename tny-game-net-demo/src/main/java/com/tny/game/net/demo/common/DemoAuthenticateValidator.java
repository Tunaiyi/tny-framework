package com.tny.game.net.demo.common;

import com.tny.game.net.command.auth.*;
import com.tny.game.net.demo.common.dto.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.springframework.stereotype.Component;

import javax.xml.bind.ValidationException;
import java.time.Instant;
import java.util.List;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 */
@Component
public class DemoAuthenticateValidator implements AuthenticateValidator<Long> {

    @Override
    public Certificate<Long> validate(Tunnel<Long> tunnel, Message message, CertificateFactory<Long> factory)
            throws CommandException, ValidationException {
        Object value = message.bodyAs(Object.class);
        if (value instanceof List) {
            List paramList = as(value);
            return factory.certificate(as(paramList.get(0)), as(paramList.get(1)), Certificates.DEFAULT_USER_TYPE, Instant.now());
        }
        if (value instanceof LoginDTO) {
            LoginDTO dto = as(value);
            return factory.certificate(dto.getCertId(), dto.getUserId(), Certificates.DEFAULT_USER_TYPE, Instant.now());
        }
        if (value instanceof UserLoginResultDTO) {
            UserLoginResultDTO dto = as(value);
            return factory.certificate(System.currentTimeMillis(), dto.getUserId(), Certificates.DEFAULT_USER_TYPE, Instant.now());
        }
        throw new ValidationException("登录失败");
    }

}

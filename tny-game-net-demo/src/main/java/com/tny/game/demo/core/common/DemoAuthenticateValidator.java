package com.tny.game.demo.core.common;

import com.tny.game.demo.core.common.dto.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.*;
import com.tny.game.net.command.auth.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 */
@Component
public class DemoAuthenticateValidator implements AuthenticateValidator<Long, CertificateFactory<Long>> {

    public DemoAuthenticateValidator() {
        System.out.println("DemoAuthenticateValidator");
    }

    @Override
    public Certificate<Long> validate(Tunnel<Long> tunnel, Message message, CertificateFactory<Long> factory)
            throws CommandException, ValidationException {
        Object value = message.bodyAs(Object.class);
        if (value instanceof List) {
            List<Object> paramList = as(value);
            return factory.certificate(as(paramList.get(0)), as(paramList.get(1)), as(paramList.get(1)), NetMessagerType.DEFAULT_USER,
                    Instant.now());
        }
        if (value instanceof LoginDTO) {
            LoginDTO dto = as(value);
            return factory.certificate(dto.getCertId(), dto.getUserId(), dto.getUserId(), NetMessagerType.DEFAULT_USER, Instant.now());
        }
        if (value instanceof LoginResultDTO) {
            LoginResultDTO dto = as(value);
            return factory.certificate(System.currentTimeMillis(), dto.getUserId(), dto.getUserId(), NetMessagerType.DEFAULT_USER, Instant.now());
        }
        System.out.println(value);
        throw new ValidationException("登录失败");
    }

}

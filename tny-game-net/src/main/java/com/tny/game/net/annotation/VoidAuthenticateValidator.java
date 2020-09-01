package com.tny.game.net.annotation;

import com.tny.game.net.command.auth.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

import javax.xml.bind.ValidationException;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2020/9/1 10:02 下午
 */
public class VoidAuthenticateValidator<T> implements AuthenticateValidator<T> {

    @Override
    public Certificate<T> validate(Tunnel<T> tunnel, Message<T> message) throws CommandException, ValidationException {
        throw new UnsupportedOperationException();
    }

}

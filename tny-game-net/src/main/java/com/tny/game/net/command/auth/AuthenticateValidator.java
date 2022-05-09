package com.tny.game.net.command.auth;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.command.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

/**
 * Session登录验证器
 *
 * @author KGTny
 */
@UnitInterface
public interface AuthenticateValidator<UID, F extends CertificateFactory<UID>> {

    /**
     * 验证Session登录, 返回带有验证结果的Session对象
     *
     * @param message 与Session相对应的链接通道
     * @return 带有验证结果的Session对象
     * @throws CommandException
     */
    Certificate<UID> validate(Tunnel<UID> tunnel, Message message, F factory) throws CommandException, ValidationException;

}

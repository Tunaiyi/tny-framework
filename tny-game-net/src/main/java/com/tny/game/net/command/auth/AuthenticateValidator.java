package com.tny.game.net.command.auth;

import com.tny.game.common.unit.annotation.UnitInterface;
import com.tny.game.net.exception.CommandException;
import com.tny.game.net.message.Message;
import com.tny.game.net.transport.Certificate;
import com.tny.game.net.transport.Tunnel;

import javax.xml.bind.ValidationException;

/**
 * Session登录验证器
 *
 * @author KGTny
 */
@UnitInterface
public interface AuthenticateValidator<UID> {

    /**
     * 验证Session登录, 返回带有验证结果的Session对象
     *
     * @param message 与Session相对应的链接通道
     * @return 带有验证结果的Session对象
     * @throws CommandException
     */
    Certificate<UID> validate(Tunnel<UID> tunnel, Message<UID> message) throws CommandException, ValidationException;

}

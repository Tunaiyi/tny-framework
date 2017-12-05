package com.tny.game.net.command.checker;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.base.CoreResponseCode;
import com.tny.game.net.base.NetLogger;
import com.tny.game.net.command.dispatcher.ControllerHolder;
import com.tny.game.net.message.Message;
import com.tny.game.net.message.sign.MessageMD5Signer;
import com.tny.game.net.tunnel.Tunnel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Kun Yang on 2017/12/5.
 */
public class MessageMD5Checker<UID> implements ControllerChecker<UID, Object> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(NetLogger.CHECKER);

    protected MessageMD5Signer<UID> md5Signer;

    protected MessageMD5Signer<UID> getMd5Signer() {
        return md5Signer;
    }

    public MessageMD5Checker(MessageMD5Signer<UID> md5Signer) {
        this.md5Signer = md5Signer;
    }

    protected boolean isCheck(Message<?> message) {
        return true;
    }

    @Override
    public ResultCode check(Tunnel<UID> tunnel, Message<UID> message, ControllerHolder holder, Object attribute) {
        if (!isCheck(message))
            return ResultCode.SUCCESS;
        try {
            return md5Signer.isVerify(tunnel, message) ? ResultCode.SUCCESS : CoreResponseCode.FALSIFY;
        } catch (Throwable e) {
            LOGGER.error("", e);
        }
        return CoreResponseCode.FALSIFY;
    }


}

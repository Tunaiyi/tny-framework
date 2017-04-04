package com.tny.game.net.common.checker;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.base.CoreResponseCode;
import com.tny.game.net.base.NetLogger;
import com.tny.game.net.tunnel.Tunnel;
import com.tny.game.net.checker.ControllerChecker;
import com.tny.game.net.checker.MessageSignGenerator;
import com.tny.game.net.common.dispatcher.ControllerHolder;
import com.tny.game.net.message.Message;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

public abstract class MessageMD5Signer<UID> implements ControllerChecker<UID, Object>, MessageSignGenerator<UID> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(NetLogger.CHECKER);

    protected boolean isCheck(Message<?> message) {
        return true;
    }

    @Override
    public ResultCode check(Tunnel<UID> tunnel, Message<UID> message, ControllerHolder holder, Object attribute) {
        if (!isCheck(message))
            return ResultCode.SUCCESS;
        String requestStr = this.createSign(tunnel, message);
        try {
            if (requestStr != null) {
                String checkKey = DigestUtils.md5Hex(requestStr.getBytes("utf-8"));
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("请求MD5key:{} 请求内容:{} 校验MD5key:{}", message.getSign(), requestStr, checkKey);
                if (!checkKey.equals(message.getSign())) {
                    LOGGER.warn("请求MD5key:{} 请求内容:{} | 校验MD5key:{} | 请求校验失败!", message.getSign(), requestStr, checkKey);
                    return CoreResponseCode.FALSIFY;
                }
                return ResultCode.SUCCESS;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return CoreResponseCode.FALSIFY;
    }

    @Override
    public String generate(Tunnel<UID> tunnel, Message<UID> message) {
        String requestStr = this.createSign(tunnel, message);
        try {
            if (requestStr != null) {
                String checkKey = DigestUtils.md5Hex(requestStr.getBytes("utf-8"));
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("编码请求MD5key:{} 请求内容:{}", checkKey, requestStr);
                return checkKey;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected abstract String createSign(Tunnel<UID> tunnel, Message<UID> message);

}

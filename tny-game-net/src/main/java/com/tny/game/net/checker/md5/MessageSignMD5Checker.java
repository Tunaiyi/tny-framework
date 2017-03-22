package com.tny.game.net.checker.md5;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.base.AppContext;
import com.tny.game.net.base.CoreResponseCode;
import com.tny.game.net.base.NetLogger;
import com.tny.game.net.checker.ControllerChecker;
import com.tny.game.net.checker.MessageSignGenerator;
import com.tny.game.net.common.dispatcher.ControllerHolder;
import com.tny.game.net.message.Message;
import com.tny.game.net.session.Session;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

public abstract class MessageSignMD5Checker<UID> implements ControllerChecker<UID, Object>, MessageSignGenerator<UID> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(NetLogger.CHECKER);

    protected boolean isCheck(Message<?> message) {
        return true;
    }

    @Override
    public ResultCode check(Session<UID> session, Message<UID> message, ControllerHolder holder, AppContext context, Object attribute) {
        if (!isCheck(message))
            return ResultCode.SUCCESS;
        String requestStr = this.createSign(message);
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
    public String generate(Session<UID> session, Message<UID> request) {
        String requestStr = this.createSign(request);
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

    protected abstract String createSign(Message<?> message);

}

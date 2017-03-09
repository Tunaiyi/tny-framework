package com.tny.game.net.checker.md5;

import com.tny.game.common.result.ResultCode;
import com.tny.game.log.CoreLogger;
import com.tny.game.net.base.AppContext;
import com.tny.game.net.base.CoreResponseCode;
import com.tny.game.net.base.Message;
import com.tny.game.net.checker.ControllerChecker;
import com.tny.game.net.checker.MessageSignGenerator;
import com.tny.game.net.dispatcher.ControllerHolder;
import com.tny.game.net.dispatcher.Request;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

public abstract class MessageSignMD5Checker implements ControllerChecker, MessageSignGenerator {

    protected static final Logger LOGGER = LoggerFactory.getLogger(CoreLogger.CHECKER);

    protected boolean isCheck(Message<?> message) {
        return true;
    }

    @Override
    public ResultCode check(Message message, ControllerHolder holder, AppContext context, Object attribute) {
        if (!isCheck(message))
            return ResultCode.SUCCESS;
        String requestStr = this.createSign(message);
        try {
            if (requestStr != null) {
                String checkKey = DigestUtils.md5Hex(requestStr.getBytes("utf-8"));
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("请求MD5key:{} 请求内容:{} 校验MD5key:{}", message.getCheckCode(), requestStr, checkKey);
                if (!checkKey.equals(message.getCheckCode())) {
                    LOGGER.warn("请求MD5key:{} 请求内容:{} | 校验MD5key:{} | 请求校验失败!", message.getCheckCode(), requestStr, checkKey);
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
    public String generate(Request request) {
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

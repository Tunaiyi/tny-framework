package com.tny.game.net.checker.md5;

import com.tny.game.common.result.ResultCode;
import com.tny.game.log.CoreLogger;
import com.tny.game.net.base.CoreResponseCode;
import com.tny.game.net.checker.RequestChecker;
import com.tny.game.net.checker.RequestVerifier;
import com.tny.game.net.dispatcher.Request;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

public abstract class MD5VerifyChecker implements RequestChecker, RequestVerifier {

    protected static final Logger LOGGER = LoggerFactory.getLogger(CoreLogger.CHECKER);

    @Override
    public ResultCode match(Request request) {
        // if (request == null || request.getCheckKey() == null)
        //     return ResultCode.SUCCESS;
        String requestStr = this.createCheckKey(request);
        try {
            if (requestStr != null) {
                String checkKey = DigestUtils.md5Hex(requestStr.getBytes("utf-8"));
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("请求MD5key:{} 请求内容:{} 校验MD5key:{}", request.getCheckKey(), requestStr, checkKey);
                if (!checkKey.equals(request.getCheckKey())) {
                    LOGGER.warn("请求MD5key:{} 请求内容:{} | 校验MD5key:{} | 请求校验失败!", request.getCheckKey(), requestStr, checkKey);
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
        String requestStr = this.createCheckKey(request);
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

    protected abstract String createCheckKey(Request request);

}

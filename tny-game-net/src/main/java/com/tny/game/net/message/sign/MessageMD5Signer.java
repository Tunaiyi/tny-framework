package com.tny.game.net.message.sign;

import com.tny.game.common.utils.Throws;
import com.tny.game.suite.app.NetLogger;
import com.tny.game.net.message.Message;
import com.tny.game.net.tunnel.Tunnel;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

public abstract class MessageMD5Signer<UID> implements MessageSignGenerator<UID> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(NetLogger.CHECKER);

    public boolean isVerify(Tunnel<UID> tunnel, Message<UID> message) throws UnsupportedEncodingException {
        String requestStr = this.createSign(tunnel, message);
        Throws.checkNotNull(requestStr);
        String checkKey = DigestUtils.md5Hex(requestStr.getBytes("utf-8"));
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("请求MD5key:{} 请求内容:{} 校验MD5key:{}", message.getSign(), requestStr, checkKey);
        if (!checkKey.equals(message.getSign())) {
            LOGGER.warn("请求MD5key:{} 请求内容:{} | 校验MD5key:{} | 请求校验失败!", message.getSign(), requestStr, checkKey);
            return false;
        }
        return true;
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

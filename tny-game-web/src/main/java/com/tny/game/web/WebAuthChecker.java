package com.tny.game.web;

import com.tny.game.suite.base.BytesAide;
import com.tny.game.suite.base.md5.MD5;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by Kun Yang on 2017/6/16.
 */
public interface WebAuthChecker {

    Logger LOGGER = LoggerFactory.getLogger(WebAuthChecker.class);

    static String sign(String host, String url, byte[] data, long date, String key) {
        int size = key.length() + url.length() + host.length() + (data == null ? 0 : data.length) + 16;
        try (ByteArrayOutputStream output = new ByteArrayOutputStream(size)) {
            IOUtils.write(url, output, "UTF-8");
            IOUtils.write(host, output, "UTF-8");
            if (data != null)
                IOUtils.write(data, output);
            IOUtils.write(BytesAide.long2Bytes(date), output);
            IOUtils.write(key, output, "UTF-8");
            return MD5.md5(output.toByteArray());
        } catch (IOException e) {
            LOGGER.debug("sign exception", e);
            throw new RuntimeException(e);
        }
    }

    static boolean verify(String host, String url, byte[] data, long date, String key, String sign) {
        String checkSign = sign(host, url, data, date, key);
        return sign.equals(checkSign);
    }
}

package com.tny.game.common.utils.digest.md5;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5 {

    public static String md5(String content) {
        return DigestUtils.md5Hex(content);
    }

    public static String md5(byte[] content) {
        return DigestUtils.md5Hex(content);
    }

}

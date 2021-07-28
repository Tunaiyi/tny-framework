package com.tny.game.redisson.utils;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2019-10-08 12:16
 */
public interface RedisKeys {

    String LINK = ":";

    String PROJECT_HEAD = "uc";

    RedisKey HEAD = RedisKey.keyOf(PROJECT_HEAD);

    RedisKey COMMON_HEAD = HEAD.dot("common");

}

package com.tny.game.common.utils.digest;

import java.util.Map;

/**
 * Created by Kun Yang on 2017/7/3.
 */
public interface ParamsSignable<K> {

    String sign(Map<String, ?> params, String words, K key) throws Throwable;

}

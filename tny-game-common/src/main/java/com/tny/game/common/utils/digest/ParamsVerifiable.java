package com.tny.game.common.utils.digest;

/**
 * Created by Kun Yang on 2017/7/3.
 */
public interface ParamsVerifiable<K> {

    boolean verify(String sign, String words, K key) throws Throwable;

}

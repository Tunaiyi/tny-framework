package com.tny.game.asyndb;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * Created by Kun Yang on 2018/1/16.
 */
public class ObjectKey {

    private Object[] keyWords;

    @Override
    public String toString() {
        return StringUtils.join(keyWords, ":");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ObjectKey)) {
            return false;
        }
        ObjectKey objectKey = (ObjectKey)o;
        return Arrays.equals(keyWords, objectKey.keyWords);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(keyWords);
    }

}

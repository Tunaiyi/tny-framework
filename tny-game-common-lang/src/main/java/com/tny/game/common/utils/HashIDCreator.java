package com.tny.game.common.utils;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Kun Yang on 16/6/6.
 */
public class HashIDCreator implements IdCreator {

    private final AtomicLong[] creators;

    private int indexOffset;

    public HashIDCreator(int size) {
        this.creators = new AtomicLong[size];
        long startAt = System.currentTimeMillis();
        for (int index = 0; index < this.creators.length; index++) {
            this.creators[index] = new AtomicLong(startAt);
        }
        int indexSize = String.valueOf(size).length();
        this.indexOffset = 1;
        for (int i = 0; i < indexSize; i++)
            this.indexOffset *= 10;
    }

    @Override
    public long createId() {
        int index = Math.abs(Thread.currentThread().hashCode() % this.creators.length);
        return this.creators[index].incrementAndGet() * this.indexOffset + index;
    }

    public String getHexId() {
        return Long.toHexString(createId());
    }

}

package com.tny.game.common.utils;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Kun Yang on 16/6/6.
 */
public class IDCreator {

    private AtomicLong[] creators;

    private int indexOffset;

    public IDCreator(int size) {
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

    public long getId() {
        int index = Math.abs(Thread.currentThread().hashCode() % this.creators.length);
        return this.creators[index].incrementAndGet() * this.indexOffset + index;
    }

    public String getHexId() {
        return Long.toHexString(getId());
    }

}

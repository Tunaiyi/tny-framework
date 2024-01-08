package com.tny.game.delayqueue;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2020/7/23 7:46 下午
 */
public class DelayMessage<T> {

    private String id;

    private String topic;

    private T body;

    private String hashKey;

    private long triggerTime;

    public String getId() {
        return id;
    }

    public String getHashKey() {
        return hashKey;
    }

    public long getTriggerTime() {
        return triggerTime;
    }

}

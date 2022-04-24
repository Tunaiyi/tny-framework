package com.tny.game.basics.item.capacity;

/**
 * 游戏能力值提供器
 * Created by Kun Yang on 16/2/15.
 */
public interface ExpireCapable extends Capable {

    static long expireAtOf(Capable capable, long expireAt) {
        return expireAt != 0 ? expireAt :
                capable instanceof ExpireCapable ? ((ExpireCapable)capable).getExpireAt() : -1;
    }

    long getExpireAt();

    boolean isExpire();

    default long getRemainTime() {
        return getRemainTime(System.currentTimeMillis());
    }

    long getRemainTime(long now);

}
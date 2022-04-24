package com.tny.game.suite.base.capacity;

/**
 * 游戏能力值提供器
 * Created by Kun Yang on 16/2/15.
 */
public interface ExpireCapacitiable extends Capacitiable {

    static long expireAtOf(Capacitiable capacitiable, long expireAt) {
        return expireAt != 0 ? expireAt :
                capacitiable instanceof ExpireCapacitiable ? ((ExpireCapacitiable)capacitiable).getExpireAt() : -1;
    }

    long getExpireAt();

    boolean isExpire();

    default long getRemainTime() {
        return getRemainTime(System.currentTimeMillis());
    }

    long getRemainTime(long now);

}
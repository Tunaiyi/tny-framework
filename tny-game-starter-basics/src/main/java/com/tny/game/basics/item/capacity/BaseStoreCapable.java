package com.tny.game.basics.item.capacity;

/**
 * 游戏能力值提供器
 * Created by Kun Yang on 16/2/15.
 */
public abstract class BaseStoreCapable implements ExpireCapable {

	private long expireAt;

	public BaseStoreCapable(long expireAt) {
		this.expireAt = expireAt;
	}

	@Override
	public long getExpireAt() {
		return expireAt;
	}

	@Override
	public boolean isExpire() {
		return expireAt >= 0 && System.currentTimeMillis() > expireAt;
	}

	@Override
	public long getRemainTime(long now) {
		if (expireAt < 0) {
			return -1;
		}
		return Math.min(expireAt - now, 0);
	}

	public void expireAt(long at) {
		this.expireAt = at;
	}

}
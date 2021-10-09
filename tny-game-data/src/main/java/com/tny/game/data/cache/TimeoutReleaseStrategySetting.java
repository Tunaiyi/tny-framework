package com.tny.game.data.cache;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/27 11:57 上午
 */
public class TimeoutReleaseStrategySetting {

	private long life;

	public long getLife() {
		return life;
	}

	public TimeoutReleaseStrategySetting setLife(long life) {
		this.life = life;
		return this;
	}

}

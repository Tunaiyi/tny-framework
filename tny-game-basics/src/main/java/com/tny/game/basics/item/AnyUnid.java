package com.tny.game.basics.item;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.*;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/15 4:50 上午
 */
public class AnyUnid implements Comparable<AnyUnid> {

	/**
	 * 玩家id
	 */
	private final long playerId;

	/**
	 * item 对象 id
	 */
	private final long id;

	public static AnyUnid uidOf(Any item) {
		return new AnyUnid(item);
	}

	public static AnyUnid uidOf(long playerId) {
		return new AnyUnid(playerId, playerId);
	}

	public static AnyUnid uidOf(long playerId, long id) {
		return new AnyUnid(playerId, id);
	}

	public static Collection<AnyUnid> uidsOf(Collection<? extends Item<?>> items) {
		return items.stream().map(AnyUnid::uidOf).collect(Collectors.toList());
	}

	public static AnyUnid parseUuid(String unid) {
		String[] values = StringUtils.split(unid, "-");
		long playerId = Long.parseUnsignedLong(values[0], 32);
		if (values.length == 1) {
			return new AnyUnid(playerId, playerId);
		}
		long id = Long.parseUnsignedLong(values[1], 32);
		return new AnyUnid(playerId, id);
	}

	/**
	 * @return 转化为 String 的 uid
	 */
	public static String formatUuid(Any any) {
		return formatUuid(any.getPlayerId(), any.getId());
	}

	/**
	 * @return 转化为 String 的 uid
	 */
	public static String formatUuid(long playerId, long id) {
		String tail = Long.toUnsignedString(id, 32);
		if (playerId == id) {
			return String.valueOf(playerId);
		}
		return playerId + "-" + tail;
	}

	private AnyUnid(Any any) {
		this.id = any.getId();
		this.playerId = any.getPlayerId();
	}

	private AnyUnid(long playerId, long id) {
		this.playerId = playerId;
		this.id = id;
	}

	/**
	 * @return 获取玩家id
	 */
	public long getPlayerId() {
		return playerId;
	}

	/**
	 * @return 对象 ID
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return 转化为 String 的 uid
	 */
	public String toUuid() {
		return formatUuid(this.playerId, this.id);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof AnyUnid)) {
			return false;
		}

		AnyUnid itemKey = (AnyUnid)o;

		return new EqualsBuilder().append(getPlayerId(), itemKey.getPlayerId())
				.append(getId(), itemKey.getId())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(getPlayerId()).append(getId()).toHashCode();
	}

	@Override
	public String toString() {
		return playerId + ":" + id;
	}

	@Override
	public int compareTo(AnyUnid o) {
		long value = this.playerId - o.playerId;
		if (value != 0) {
			return value > 0 ? 1 : 0;
		}
		value = this.id - o.id;
		if (value == 0) {
			return 0;
		}
		return value > 0 ? 1 : 0;
	}

}

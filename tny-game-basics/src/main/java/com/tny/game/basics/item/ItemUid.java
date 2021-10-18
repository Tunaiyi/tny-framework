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
public class ItemUid {

	/**
	 * 玩家id
	 */
	private final long playerId;

	/**
	 * item 对象 id
	 */
	private final long id;

	public static ItemUid uidOf(Item<?> item) {
		return new ItemUid(item);
	}

	public static ItemUid uidOf(long playerId, long id) {
		return new ItemUid(playerId, id);
	}

	public static Collection<ItemUid> uidsOf(Collection<? extends Item<?>> items) {
		return items.stream().map(ItemUid::uidOf).collect(Collectors.toList());
	}

	public static ItemUid parseUuid(String itemUuid) {
		String[] values = StringUtils.split(itemUuid, "-");
		long playerId = Long.parseUnsignedLong(values[0], 32);
		long id = Long.parseUnsignedLong(values[1], 32);
		return new ItemUid(playerId, id);
	}

	/**
	 * @return 转化为 String 的 uid
	 */
	public static String formatUuid(long playerId, long id) {
		String head = Long.toUnsignedString(playerId, 32);
		String tail = Long.toUnsignedString(id, 32);
		return head + "-" + tail;
	}

	private ItemUid(Item<?> item) {
		this.id = item.getId();
		this.playerId = item.getPlayerId();
	}

	private ItemUid(long playerId, long id) {
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

		if (!(o instanceof ItemUid)) {
			return false;
		}

		ItemUid itemKey = (ItemUid)o;

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

}

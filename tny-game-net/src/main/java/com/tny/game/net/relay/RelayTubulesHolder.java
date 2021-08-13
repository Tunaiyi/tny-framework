package com.tny.game.net.relay;

import java.rmi.server.UID;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/4/28 10:34 下午
 */
public class RelayTubulesHolder {

	private String userType;

	private final Map<UID, NetRelayTubule<UID>> tubuleMap = new ConcurrentHashMap<>();

	public Optional<RelayTubule<UID>> getTubuleByUserId(UID userId) {
		return Optional.ofNullable(this.tubuleMap.get(userId));
	}

}

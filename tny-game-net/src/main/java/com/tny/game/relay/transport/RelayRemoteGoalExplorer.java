package com.tny.game.relay.transport;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/21 11:45 上午
 */
public class RelayRemoteGoalExplorer {

	private final Map<String, RelayRemoteGoal> goalMap = new ConcurrentHashMap<>();

}

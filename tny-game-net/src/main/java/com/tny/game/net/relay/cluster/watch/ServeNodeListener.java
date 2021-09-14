package com.tny.game.net.relay.cluster.watch;

import com.tny.game.net.relay.cluster.*;

import java.util.List;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/10 2:58 下午
 */
public interface ServeNodeListener {

	void onChange(ServeNode node, List<ServeNodeChangeStatus> statuses);

	void onRemove(ServeNode node);

	void onCreate(ServeNode node);

}

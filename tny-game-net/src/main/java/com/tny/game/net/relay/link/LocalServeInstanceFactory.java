package com.tny.game.net.relay.link;

import com.tny.game.net.relay.cluster.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/2 5:47 下午
 */
public interface LocalServeInstanceFactory<T extends LocalServeInstance> {

	T create(LocalServeCluster cluster, ServeNode node);

}

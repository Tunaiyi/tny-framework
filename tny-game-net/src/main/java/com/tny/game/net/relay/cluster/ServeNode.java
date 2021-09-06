package com.tny.game.net.relay.cluster;

import com.tny.game.common.url.*;

import javax.annotation.Nonnull;
import java.util.Comparator;

/**
 * 集群节点信息
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/23 9:21 下午
 */
public interface ServeNode extends Comparable<ServeNode> {

	Comparator<ServeNode> COMPARATOR = Comparator.comparingLong(ServeNode::getId);

	/**
	 * @return 集群 id
	 */
	String getClusterId();

	/**
	 * @return 集群实例 id
	 */
	long getId();

	/**
	 * @return 网络协议 http/https/tcp
	 */
	String getScheme();

	/**
	 * @return host
	 */
	String getHost();

	/**
	 * @return 端口
	 */
	int getPort();

	/**
	 * @return 获取地址
	 */
	default URL url() {
		return new URL(this.getScheme(), this.getHost(), this.getPort());
	}

	@Override
	default int compareTo(@Nonnull ServeNode o) {
		return COMPARATOR.compare(this, o);
	}

}

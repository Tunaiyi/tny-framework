package com.tny.game.net.relay.cluster;

import com.tny.game.common.collection.map.access.*;
import com.tny.game.common.url.*;
import com.tny.game.net.base.*;

import javax.annotation.Nonnull;
import java.util.*;

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
	 * @return 集群 ServeName
	 */
	String getServeName();

	/**
	 * @return 集群服务实例 id
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
	 * @return app 类型
	 */
	String getAppType();

	/**
	 * @return scope 类型
	 */
	String getScopeType();

	/**
	 * @return 是否监控
	 */
	boolean isHealthy();

	/**
	 * @return app 类型枚举
	 */
	default AppType appType() {
		return AppTypes.of(this.getAppType());
	}

	/**
	 * @return scope 类型枚举
	 */
	default ScopeType scopeType() {
		return ScopeTypes.of(this.getScopeType());
	}

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

	/**
	 * @return 获取元数据
	 */
	MapAccessor metadata();

	/**
	 * @return 获取元数据
	 */
	Map<String, Object> getMetadata();

	@Override
	default int compareTo(@Nonnull ServeNode o) {
		return COMPARATOR.compare(this, o);
	}

}

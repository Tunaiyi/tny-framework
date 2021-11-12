package com.tny.game.net.relay.cluster;

import com.tny.game.net.base.*;

import javax.annotation.Nonnull;
import java.util.Comparator;

/**
 * 集群节点信息
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/23 9:21 下午
 */
public interface ServeNode extends NetAccessPoint, Comparable<ServeNode> {

	Comparator<ServeNode> COMPARATOR = Comparator.comparingLong(ServeNode::getId);

	/**
	 * @return 集群 ServeName
	 */
	String getServeName();

	/**
	 * @return app 类型
	 */
	String getAppType();

	/**
	 * @return scope 类型
	 */
	String getScopeType();

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

	@Override
	default int compareTo(@Nonnull ServeNode o) {
		return COMPARATOR.compare(this, o);
	}

}

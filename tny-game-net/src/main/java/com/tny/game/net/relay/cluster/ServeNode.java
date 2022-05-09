package com.tny.game.net.relay.cluster;

import com.tny.game.net.base.*;
import com.tny.game.net.serve.*;

import javax.annotation.Nonnull;
import java.util.Comparator;

/**
 * 集群节点信息
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/23 9:21 下午
 */
public interface ServeNode extends NetAccessNode, Serve, Comparable<ServeNode> {

    Comparator<ServeNode> COMPARATOR = Comparator.comparingLong(ServeNode::getId);

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
        return AppTypes.ofAppName(this.getAppType());
    }

    /**
     * @return scope 类型枚举
     */
    default AppScope scopeType() {
        return AppScopes.ofScopeName(this.getScopeType());
    }

    @Override
    default int compareTo(@Nonnull ServeNode o) {
        return COMPARATOR.compare(this, o);
    }

}

/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
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
    /**
     * @return 服务类型
     */

    default RpcServiceType serviceType() {
        return RpcServiceTypes.checkService(serviceName());
    }

    @Override
    default int compareTo(@Nonnull ServeNode o) {
        return COMPARATOR.compare(this, o);
    }

}

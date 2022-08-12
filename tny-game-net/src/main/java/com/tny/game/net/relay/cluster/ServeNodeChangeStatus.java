/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.relay.cluster;

import java.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/10 6:04 下午
 */
public enum ServeNodeChangeStatus {

    RENEW_NODE {
        @Override
        public boolean isChange(RemoteServeNode oldOne, RemoteServeNode newOne) {
            return oldOne.getLaunchTime() != newOne.getLaunchTime();
        }
    },

    URL_CHANGE {
        @Override
        public boolean isChange(RemoteServeNode oldOne, RemoteServeNode newOne) {
            return !Objects.equals(oldOne.getScheme(), newOne.getScheme()) ||
                    !Objects.equals(oldOne.getHost(), newOne.getHost()) ||
                    !Objects.equals(oldOne.getPort(), newOne.getPort());
        }
    },

    METADATA_CHANGE {
        @Override
        public boolean isChange(RemoteServeNode oldOne, RemoteServeNode newOne) {
            return !Objects.equals(oldOne.isHealthy(), newOne.isHealthy()) ||
                    !Objects.equals(oldOne.getMetadata(), newOne.getMetadata());
        }
    };

    public abstract boolean isChange(RemoteServeNode oldOne, RemoteServeNode newOne);

    public static List<ServeNodeChangeStatus> checkChange(RemoteServeNode oldOne, RemoteServeNode newOne) {
        List<ServeNodeChangeStatus> changeStatuses = new ArrayList<>();
        for (ServeNodeChangeStatus status : ServeNodeChangeStatus.values()) {
            if (status.isChange(oldOne, newOne)) {
                changeStatuses.add(status);
            }
        }
        return changeStatuses;
    }

}

/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

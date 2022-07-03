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

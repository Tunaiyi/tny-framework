package com.tny.game.net.relay.cluster;

import java.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/10 6:04 下午
 */
public enum ServeNodeChangeStatus {

    URL_CHANGE {
        @Override
        public boolean isChange(ServeNode oldOne, ServeNode newOne) {
            return !Objects.equals(oldOne.getScheme(), newOne.getScheme()) ||
                    !Objects.equals(oldOne.getHost(), newOne.getHost()) ||
                    !Objects.equals(oldOne.getPort(), newOne.getPort());
        }
    },

    METADATA_CHANGE {
        @Override
        public boolean isChange(ServeNode oldOne, ServeNode newOne) {
            return !Objects.equals(oldOne.isHealthy(), newOne.isHealthy()) ||
                    !Objects.equals(oldOne.getMetadata(), newOne.getMetadata());
        }
    };

    public abstract boolean isChange(ServeNode oldOne, ServeNode newOne);

    public static List<ServeNodeChangeStatus> checkChange(ServeNode oldOne, ServeNode newOne) {
        List<ServeNodeChangeStatus> changeStatuses = new ArrayList<>();
        for (ServeNodeChangeStatus status : ServeNodeChangeStatus.values()) {
            if (status.isChange(oldOne, newOne)) {
                changeStatuses.add(status);
            }
        }
        return changeStatuses;
    }

}

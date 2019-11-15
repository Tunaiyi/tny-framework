package com.tny.game.suite.cluster.event;

import com.tny.game.suite.cluster.*;

public interface ServiceNodeHolderListener {

    default void onAdd(ServiceNodeHolder holder, ServiceNode node) {
    }

    default void onRemove(ServiceNodeHolder holder, ServiceNode node) {
    }

    default void onChange(ServiceNodeHolder holder, ServiceNode newNode, ServiceNode oldNode) {
    }

}

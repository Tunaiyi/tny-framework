package com.tny.game.suite.cluster.event;

import com.tny.game.suite.cluster.ServiceNode;
import com.tny.game.suite.cluster.ServiceNodeHolder;

public interface ServiceNodeHolderListener {

    default void onAdd(ServiceNodeHolder holder, ServiceNode node) {
    }

    default void onRemove(ServiceNodeHolder holder, ServiceNode node) {
    }

    default void onChange(ServiceNodeHolder holder, ServiceNode newNode, ServiceNode oldNode) {
    }

}

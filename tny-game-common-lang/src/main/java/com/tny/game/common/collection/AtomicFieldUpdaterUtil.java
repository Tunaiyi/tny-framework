/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.common.collection;

import java.util.concurrent.atomic.*;

/**
 * @author <a href="http://www.jboss.org/netty/">The Netty Project</a>
 * @author <a href="http://gleamynode.net/">Trustin Lee</a>
 * @version $Rev: 2080 $, $Date: 2010-01-26 18:04:19 +0900 (Tue, 26 Jan 2010) $
 */
class AtomicFieldUpdaterUtil {

    private static final boolean AVAILABLE;

    /**
     * @author KGTny
     */
    static final class Node {

        /**
         * @uml.property name="next"
         * @uml.associationEnd
         */
        volatile Node next;

        Node() {
            super();
        }

    }

    static {
        boolean available = false;
        try {
            AtomicReferenceFieldUpdater<Node, Node> tmp = AtomicReferenceFieldUpdater.newUpdater(Node.class, Node.class, "next");

            // Test if AtomicReferenceFieldUpdater is really working.
            Node testNode = new Node();
            tmp.set(testNode, testNode);
            if (testNode.next != testNode) {
                // Not set as expected - fall back to the safe mode.
                throw new Exception();
            }
            available = true;
        } catch (Throwable t) {
            // Running in a restricted environment with a security manager.
        }
        AVAILABLE = available;
    }

    static <T, V> AtomicReferenceFieldUpdater<T, V> newRefUpdater(Class<T> tclass, Class<V> vclass, String fieldName) {
        if (AVAILABLE) {
            return AtomicReferenceFieldUpdater.newUpdater(tclass, vclass, fieldName);
        } else {
            return null;
        }
    }

    static <T> AtomicIntegerFieldUpdater<T> newIntUpdater(Class<T> tclass, String fieldName) {
        if (AVAILABLE) {
            return AtomicIntegerFieldUpdater.newUpdater(tclass, fieldName);
        } else {
            return null;
        }
    }

    static boolean isAvailable() {
        return AVAILABLE;
    }

    private AtomicFieldUpdaterUtil() {
        // Unused
    }

}

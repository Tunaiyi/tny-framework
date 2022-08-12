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

import java.util.List;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/13 12:10 下午
 */
public class ServeNodeChange {

    private final ServeNode node;

    private final List<ServeNodeChangeStatus> changeStatuses;

    public ServeNodeChange(ServeNode node, List<ServeNodeChangeStatus> changeStatuses) {
        this.node = node;
        this.changeStatuses = changeStatuses;
    }

    public ServeNode getNode() {
        return node;
    }

    public List<ServeNodeChangeStatus> getChangeStatuses() {
        return changeStatuses;
    }

}

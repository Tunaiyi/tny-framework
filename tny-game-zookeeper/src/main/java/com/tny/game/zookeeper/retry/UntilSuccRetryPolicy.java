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

package com.tny.game.zookeeper.retry;

import com.tny.game.zookeeper.*;
import org.apache.zookeeper.KeeperException.Code;

public class UntilSuccRetryPolicy extends RetryPolicy {

    private int interval;

    private boolean fail = false;

    public UntilSuccRetryPolicy(int interval) {
        super();
        this.interval = interval;
    }

    @Override
    protected void fail(Code code) {
        fail = true;
    }

    @Override
    protected void success() {
        fail = false;
    }

    @Override
    protected void reset() {
        fail = false;
    }

    @Override
    public long getDelayTime() {
        return !fail ? 0 : interval;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

}

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

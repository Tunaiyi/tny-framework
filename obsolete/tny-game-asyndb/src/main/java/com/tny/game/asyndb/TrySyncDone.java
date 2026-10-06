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
package com.tny.game.asyndb;

public class TrySyncDone {

    public static final TrySyncDone FAIL = new TrySyncDone(false);

    private boolean sync;

    private AsyncDBState state;

    private Object value;

    private TrySyncDone(boolean sync) {
        this.sync = sync;
    }

    public TrySyncDone(AsyncDBState state, Object value) {
        this.sync = state.hasOperation();
        this.state = state;
        this.value = value;
    }

    public boolean isSync() {
        return this.sync;
    }

    public AsyncDBState getState() {
        return this.state;
    }

    public Object getValue() {
        return this.value;
    }

}

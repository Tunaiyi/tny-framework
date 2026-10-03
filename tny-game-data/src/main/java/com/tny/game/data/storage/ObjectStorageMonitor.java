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

package com.tny.game.data.storage;

import java.util.concurrent.atomic.LongAdder;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/15 6:40 下午
 */
public class ObjectStorageMonitor {

    private final Class<?> objectClass;

    private final LongAdder insertCounter = new LongAdder();

    private final LongAdder updateCounter = new LongAdder();

    private final LongAdder saveCounter = new LongAdder();

    private final LongAdder deleteCounter = new LongAdder();

    private final LongAdder failedCounter = new LongAdder();

    public ObjectStorageMonitor(Class<?> objectClass) {
        this.objectClass = objectClass;
    }

    public Class<?> getObjectClass() {
        return objectClass;
    }

    public void onFailure() {
        this.failedCounter.increment();
    }

    public void onSuccess(StorageOperator operator) {
        switch (operator) {
            case INSERT:
                this.insertCounter.increment();
                break;
            case UPDATE:
                this.updateCounter.increment();
                break;
            case SAVE:
                this.saveCounter.increment();
                break;
            case DELETE:
                this.deleteCounter.increment();
                break;
        }
    }

    public long getInsertNumber() {
        return this.insertCounter.longValue();
    }

    public long getUpdateNumber() {
        return this.updateCounter.longValue();
    }

    public long getSaveNumber() {
        return this.saveCounter.longValue();
    }

    public long getDeleteNumber() {
        return this.deleteCounter.longValue();
    }

    public long getFailedNumber() {
        return this.failedCounter.longValue();
    }

}

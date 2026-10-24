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

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/15 6:14 下午
 */
public interface AsyncObjectStorage<K extends Comparable<?>, O> extends ObjectStorage<K, O> {

    ObjectStorageMonitor getMonitor();

    int queueSize();

    StoreExecuteAction store(int maxSize, int tryTimes);

    void operateAll();

}

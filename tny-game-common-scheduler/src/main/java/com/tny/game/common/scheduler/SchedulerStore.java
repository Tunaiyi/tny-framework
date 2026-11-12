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

package com.tny.game.common.scheduler;

/**
 * 调度存储接口
 *
 * @author Kun.y
 */
public interface SchedulerStore {

    /**
     * 保存存储方案
     *
     * @param timeTaskScheduler
     */
    void store(TimeTaskScheduler timeTaskScheduler);

    /**
     * 读取存储方案
     *
     * @return
     */
    SchedulerBackup restore();

}

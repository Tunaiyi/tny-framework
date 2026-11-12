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

package com.tny.game.basics.scheduler;

import com.tny.game.common.scheduler.*;

import java.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/30 12:04 下午
 */
public class TimeTasksSchemeModel {

    private List<TimeTask> timeTasks = new ArrayList<>();

    public List<TimeTask> getTimeTasks() {
        return timeTasks;
    }

    public TimeTasksSchemeModel setTimeTasks(List<TimeTask> timeTasks) {
        this.timeTasks = timeTasks;
        return this;
    }

}

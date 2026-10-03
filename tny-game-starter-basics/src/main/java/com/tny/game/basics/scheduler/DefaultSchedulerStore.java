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

import com.tny.game.basics.configuration.*;
import com.tny.game.common.scheduler.*;

public class DefaultSchedulerStore implements SchedulerStore {

    private final SchedulerBackupFactory backupFactory;

    private final SchedulerBackupManager schedulerBackupManager;

    private final BasicsTimeTaskProperties properties;

    public DefaultSchedulerStore(
            BasicsTimeTaskProperties properties,
            SchedulerBackupFactory backupFactory,
            SchedulerBackupManager schedulerBackupManager) {
        this.properties = properties;
        this.backupFactory = backupFactory;
        this.schedulerBackupManager = schedulerBackupManager;
    }

    @Override
    public void store(TimeTaskScheduler timeTaskScheduler) {
        SchedulerBackup backup = backupFactory.create(timeTaskScheduler);
        schedulerBackupManager.saveBackup(backup);
    }

    @Override
    public SchedulerBackup restore() {
        return schedulerBackupManager.getBackup(properties.getId());
    }

}

/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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

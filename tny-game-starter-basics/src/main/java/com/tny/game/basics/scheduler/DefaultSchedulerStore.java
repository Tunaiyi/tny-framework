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

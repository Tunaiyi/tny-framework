package com.tny.game.oplog;

import java.util.List;

public interface OperateLog extends Loggable {

    int getActionID();

    Object getModule();

    Object getOperation();

    String getName();

    int getLevel();

    List<? extends StuffLog> getRevs();

    List<? extends StuffLog> getCoss();

    List<? extends Snapshot> getSnaps();

    // Action getAction();
    //
    // String getFuncSysDesc();
    //
    // String getBehaviorDesc();
    //
    // String getActionDesc();
}

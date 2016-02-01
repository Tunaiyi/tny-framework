package com.tny.game.oplog;

import com.tny.game.base.item.behavior.Action;

import java.util.List;

public interface OperateLog extends Logable {

    int getActionID();

    Object getModule();

    Object getOperation();

    String getName();

    Action getAction();

    String getFuncSysDesc();

    String getBehaviorDesc();

    String getActionDesc();

    int getLevel();

    List<? extends StuffLog> getRevs();

    List<? extends StuffLog> getCoss();

    List<? extends Snapshot> getSnaps();

}

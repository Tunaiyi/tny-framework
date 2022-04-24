package com.tny.game.basics.scheduler;

import com.tny.game.common.scheduler.*;

public interface GameTaskReceiverManager {

    GameTaskReceiver getReceiver(long playerId, TaskReceiverType receiverType);

    boolean insertReceiver(GameTaskReceiver receiver);

    boolean saveReceiver(GameTaskReceiver receiver);

}
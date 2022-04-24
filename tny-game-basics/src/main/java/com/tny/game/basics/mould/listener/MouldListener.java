package com.tny.game.basics.mould.listener;

import com.tny.game.basics.mould.*;
import com.tny.game.common.event.bus.*;

public interface MouldListener {

    BindP1EventBus<MouldListener, FeatureLauncher, Mould> OPEN_MOULD_EVENT = EventBuses.of(MouldListener.class,
            MouldListener::handleOpenMould);

    void handleOpenMould(FeatureLauncher explorer, Mould openedMould);

}
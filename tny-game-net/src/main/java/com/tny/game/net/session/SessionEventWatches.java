package com.tny.game.net.session;

import com.tny.game.common.event.*;
import com.tny.game.net.session.listener.*;

public interface SessionEventWatches {

    EventListen<SessionOnlineListener> onlineWatch();

    EventListen<SessionOfflineListener> offlineWatch();

    EventListen<SessionCloseListener> closeWatch();
}

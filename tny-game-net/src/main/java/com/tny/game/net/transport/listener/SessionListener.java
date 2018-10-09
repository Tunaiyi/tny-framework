package com.tny.game.net.transport.listener;

public interface SessionListener<UID> extends SessionAcceptListener<UID>, SessionOnlineListener<UID>, SessionOfflineListener<UID>, SessionCloseListener<UID> {



}

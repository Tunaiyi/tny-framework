package com.tny.game.actor.local.message;

import java.io.Serializable;

public interface AutoReceivedMessage extends Serializable {

    AutoReceivedMessageType getMessageType();

}

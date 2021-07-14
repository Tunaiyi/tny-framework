package com.tny.game.net.base;

import com.tny.game.net.codec.v1.*;

public interface NetBootstrapSetting {

    String getName();

    DataPacketV1Config getEncoder();

    DataPacketV1Config getDecoder();

    String getMessageFactory();

    String getCertificateFactory();

    String getMessageDispatcher();

    String getCommandTaskProcessor();

}

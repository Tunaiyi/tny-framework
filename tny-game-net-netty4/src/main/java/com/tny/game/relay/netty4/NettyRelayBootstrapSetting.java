package com.tny.game.relay.netty4;

import com.tny.game.net.base.*;
import com.tny.game.net.netty4.codec.*;

public interface NettyRelayBootstrapSetting extends NetBootstrapSetting {

	DataPacketCodecSetting getEncoder();

	DataPacketCodecSetting getDecoder();

}

package com.tny.game.net.netty4;

import com.tny.game.net.base.*;
import com.tny.game.net.netty4.codec.*;

public interface NettyBootstrapSetting extends NetBootstrapSetting {

	DataPacketCodecSetting getEncoder();

	DataPacketCodecSetting getDecoder();

}

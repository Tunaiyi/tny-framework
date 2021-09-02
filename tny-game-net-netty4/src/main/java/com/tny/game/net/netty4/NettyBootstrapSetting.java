package com.tny.game.net.netty4;

import com.tny.game.net.base.*;
import com.tny.game.net.netty4.datagram.*;

public interface NettyBootstrapSetting extends NetBootstrapSetting {

	NettyDatagramChannelSetting getChannel();

}

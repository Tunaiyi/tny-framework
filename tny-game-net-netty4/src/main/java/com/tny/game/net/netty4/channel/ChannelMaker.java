package com.tny.game.net.netty4.channel;

import com.tny.game.common.lifecycle.unit.annotation.*;
import io.netty.channel.Channel;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/31 4:18 下午
 */
@UnitInterface
public interface ChannelMaker<C extends Channel> {

    void initChannel(C channel) throws Exception;

}

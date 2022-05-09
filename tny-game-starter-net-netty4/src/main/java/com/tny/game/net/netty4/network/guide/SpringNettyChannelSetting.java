package com.tny.game.net.netty4.network.guide;

import com.tny.game.net.message.codec.*;
import com.tny.game.net.netty4.channel.*;
import com.tny.game.net.netty4.network.*;
import com.tny.game.net.netty4.network.codec.*;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import static com.tny.game.net.base.configuration.NetUnitNames.*;

public class SpringNettyChannelSetting extends NettyChannelSetting {

    @NestedConfigurationProperty
    private NettyChannelMakerSetting maker;

    @NestedConfigurationProperty
    private NetPacketCodecSetting encoder;

    @NestedConfigurationProperty
    private NetPacketCodecSetting decoder;

    public SpringNettyChannelSetting() {
        super(lowerCamelName(ProtoExMessageBodyCodec.class), lowerCamelName(ProtoExMessageBodyCodec.class));
    }

    @Override
    public NettyChannelMakerSetting getMaker() {
        return super.getMaker();
    }

    @Override
    public NetPacketCodecSetting getEncoder() {
        return super.getEncoder();
    }

    @Override
    public NetPacketCodecSetting getDecoder() {
        return super.getDecoder();
    }

    @Override
    public NettyChannelSetting setMaker(NettyChannelMakerSetting maker) {
        return super.setMaker(maker);
    }

    @Override
    public NettyChannelSetting setEncoder(NetPacketCodecSetting encoder) {
        return super.setEncoder(encoder);
    }

    @Override
    public NettyChannelSetting setDecoder(NetPacketCodecSetting decoder) {
        return super.setDecoder(decoder);
    }

}

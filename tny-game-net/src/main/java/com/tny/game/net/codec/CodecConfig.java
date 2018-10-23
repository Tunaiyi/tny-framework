package com.tny.game.net.codec;

import com.tny.game.net.message.Message;
import com.tny.game.net.message.coder.Codec;

/**
 * Created by Kun Yang on 2018/8/13.
 */
public class CodecConfig {

    private DataPacketConfig packetConfig;
    private Codec<Message<?>> messageCodec;
    private CodecVerifier verifier;
    private CodecCryptology cryptology;

    public Codec<Message<?>> getMessageCodec() {
        return messageCodec;
    }

    public DataPacketConfig getPacketConfig() {
        return packetConfig;
    }

    public CodecVerifier getVerifier() {
        return verifier;
    }

    public CodecCryptology getCryptology() {
        return cryptology;
    }

}

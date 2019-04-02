package com.tny.game.suite.net.spring;

import com.tny.game.net.codec.v1.*;
import org.springframework.beans.factory.annotation.Value;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-30 17:30
 */
public class SuitDataPacketV1Config extends DataPacketV1Config {

    @Override
    @Value("${encryptEnable:false}")
    public DataPacketV1Config setEncryptEnable(boolean encryptEnable) {
        return super.setEncryptEnable(encryptEnable);
    }

    @Override
    @Value("${wasteBytesEnable:false}")
    public DataPacketV1Config setWasteBytesEnable(boolean wasteBytesEnable) {
        return super.setWasteBytesEnable(wasteBytesEnable);
    }

    @Override
    @Value("${verifyEnable:false}")
    public DataPacketV1Config setVerifyEnable(boolean verifyEnable) {
        return super.setVerifyEnable(verifyEnable);
    }

    @Override
    @Value("${skipNumberStep:30}")
    public DataPacketV1Config setSkipNumberStep(long skipNumberStep) {
        return super.setSkipNumberStep(skipNumberStep);
    }

    @Override
    @Value("${packetTimeout:60000}")
    public DataPacketV1Config setPacketTimeout(long packetTimeout) {
        return super.setPacketTimeout(packetTimeout);
    }

    @Override
    @Value("${maxWasteBitSize:64}")
    public DataPacketV1Config setMaxWasteBitSize(int maxWasteBitSize) {
        return super.setMaxWasteBitSize(maxWasteBitSize);
    }

    @Override
    @Value("${securityKeys}")
    public DataPacketV1Config setSecurityKeys(String[] securityKeys) {
        return super.setSecurityKeys(securityKeys);
    }

    // @Override
    // @Value("${bodyCoder:ProtoExCodec}")
    // public DataPacketV1Config setMessageCodec(String messageCodec) {
    //     return super.setMessageCodec(messageCodec);
    // }


    @Override
    @Value("${bodyCodec:ProtoExCodec}")
    public DataPacketV1Config setBodyCodec(String bodyCodec) {
        return super.setBodyCodec(bodyCodec);
    }

    @Override
    @Value("${tailCodec:ProtoExCodec}")
    public DataPacketV1Config setTailCodec(String tailCodec) {
        return super.setTailCodec(tailCodec);
    }

    @Override
    public DataPacketV1Config setBodyDecodeStrategy(String bodyDecodeStrategy) {
        return super.setBodyDecodeStrategy(bodyDecodeStrategy);
    }

    @Override
    @Value("${verifier:CRC64CodecVerifier}")
    public DataPacketV1Config setVerifier(String verifier) {
        return super.setVerifier(verifier);
    }

    @Override
    @Value("${crypto:NoneCodecCrypto}")
    public DataPacketV1Config setCrypto(String crypto) {
        return super.setCrypto(crypto);
    }

}

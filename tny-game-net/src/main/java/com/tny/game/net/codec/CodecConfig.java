package com.tny.game.net.codec;

import com.tny.game.net.message.*;
import com.tny.game.net.message.codec.*;

/**
 * Created by Kun Yang on 2018/8/13.
 */
public interface CodecConfig {

    /**
     * @return 消息编解码器
     */
    MessageBodyCodec<Message> getMessageCodec();

    /**
     * @return 数据包校验器
     */
    CodecVerifier getVerifier();

    /**
     * @return 数据包加密器
     */
    CodecCrypto getCryptology();

}

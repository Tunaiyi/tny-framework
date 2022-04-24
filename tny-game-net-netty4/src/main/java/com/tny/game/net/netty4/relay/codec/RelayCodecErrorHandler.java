package com.tny.game.net.netty4.relay.codec;

import com.tny.game.common.exception.*;
import com.tny.game.common.result.*;
import com.tny.game.net.netty4.relay.*;
import com.tny.game.net.relay.link.*;
import io.netty.channel.*;
import org.slf4j.Logger;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/31 5:31 下午
 */
public interface RelayCodecErrorHandler {

    default void handleOnDecodeError(Logger logger, ChannelHandlerContext ctx, Throwable exception, boolean close) {
        handleOnError("Message解码", logger, ctx, exception, close);
    }

    default void handleOnEncodeError(Logger logger, ChannelHandlerContext ctx, Throwable exception, boolean close) {
        handleOnError("Message编码", logger, ctx, exception, close);
    }

    default void handleOnError(String action, Logger logger, ChannelHandlerContext ctx, Throwable exception, boolean close) {
        RelayLink link = null;
        Channel channel = null;
        if (ctx != null) {
            channel = ctx.channel();
            link = channel.attr(NettyRelayAttrKeys.RELAY_LINK).get();
        }
        if (channel != null) {
            if (!close) {
                ResultCode code = null;
                if (exception instanceof ResultCodeRuntimeException) {
                    code = ((ResultCodeRuntimeException)exception).getCode();
                }
                if (exception instanceof ResultCodeException) {
                    code = ((ResultCodeException)exception).getCode();
                }
                if (code != null && code.getLevel() == ResultLevel.ERROR) {
                    close = true;
                }
            }
            if (close) {
                channel.close();
            }
        }
        logger.error("# RelayLink ({}) [{}] {}异常 {}", link, channel, action, close ? ", 服务器主动关闭连接" : "", exception);
    }

}

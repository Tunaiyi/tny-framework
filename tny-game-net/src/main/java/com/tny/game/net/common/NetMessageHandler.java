package com.tny.game.net.common;

import com.tny.game.common.worker.command.Command;
import com.tny.game.net.base.AppConfiguration;
import com.tny.game.net.base.annotation.Unit;
import com.tny.game.net.command.DispatchCommandExecutor;
import com.tny.game.net.command.dispatcher.MessageDispatcher;
import com.tny.game.net.exception.DispatchException;
import com.tny.game.net.transport.*;
import com.tny.game.net.transport.message.*;
import org.slf4j.*;

/**
 * Created by Kun Yang on 2017/3/18.
 */
@Unit("NetMessageHandler")
public class NetMessageHandler<UID> implements MessageHandler<UID> {

    public static final Logger LOGGER = LoggerFactory.getLogger(NetMessageHandler.class);

    private AppConfiguration appConfiguration;

    public NetMessageHandler(AppConfiguration appConfiguration) {
        this.appConfiguration = appConfiguration;
    }

    @Override
    public void handle(NetTunnel<UID> tunnel, NetMessage<UID> message) {
        MessageDispatcher messageDispatcher = appConfiguration.getMessageDispatcher();
        DispatchCommandExecutor commandExecutor = appConfiguration.getDispatchCommandExecutor();
        if (tunnel != null) {
            try {
                Command command = messageDispatcher.dispatch(tunnel, message);
                commandExecutor.submit(tunnel, command);
            } catch (DispatchException e) {
                if (message.getMode() == MessageMode.REQUEST)
                    tunnel.sendAsyn(MessageSubjectBuilder
                            .respondBuilder(e.getResultCode(), message.getHeader())
                            .setBody(e.getBody())
                            .build());
                LOGGER.error("#ThreadPoolSessionInputEvetHandler#处理InputEvent异常", e);
            }
        }
    }

}

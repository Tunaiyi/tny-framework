package com.tny.game.net.dispatcher;

import com.tny.game.common.ExceptionUtils;
import com.tny.game.log.CoreLogger;
import com.tny.game.net.base.AppContext;
import com.tny.game.net.base.CoreResponseCode;
import com.tny.game.net.base.Message;
import com.tny.game.net.base.MessageType;
import com.tny.game.net.executor.DispatcherCommandExecutor;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.WriteTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.util.Date;

/**
 * 游戏请求处理器. 负责获取请求并将请求传给分发器
 *
 * @author KGTny
 */
@Sharable
public class MessageHandler extends SimpleChannelInboundHandler<Message> {

    protected static final Logger LOG = LoggerFactory.getLogger(CoreLogger.NET);

    /**
     * 會話持有對象
     */
    protected NetSessionHolder sessionHolder;

    /**
     * 请求命令执行器
     */
    protected DispatcherCommandExecutor commandExecutor;

    /**
     * 请求分发器
     */
    protected MessageDispatcher messageDispatcher;

    /**
     * session工厂
     */
    protected ServerSessionFactory sessionFactory;

    public MessageHandler() {

    }

    public MessageHandler(AppContext appContext) {
        this.setAppContext(appContext);
    }

    public void setAppContext(AppContext appContext) {
        this.commandExecutor = appContext.getCommandExecutor();
        this.messageDispatcher = appContext.getMessageDispatcher();
        this.sessionHolder = appContext.getSessionHolder();
        this.sessionFactory = appContext.getSessionFactory();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        if (MessageHandler.LOG.isInfoEnabled()) {
            Channel channel = ctx.channel();
            MessageHandler.LOG.info("接受连接##通道 {} ==> {} 在 {} 时链接服务器", channel.remoteAddress(), channel.localAddress(), new Date());
        }
        super.channelRegistered(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (this == ctx.pipeline().last()) {
            Channel channel = ctx.channel();
            if (cause instanceof ClosedChannelException) {
                MessageHandler.LOG.warn(this.getClass().getName() + " # java.nio.channels.ClosedChannelException # 客户端连接已断开");
            } else if (cause instanceof IOException) {
                MessageHandler.LOG.warn(this.getClass().getName() + " # java.io.IOException #" + cause.getMessage());
            } else if (cause instanceof WriteTimeoutException) {
                LOG.info("{}##通道 {} ==> {} 在 {} 时断开链接", "写出数据超时", channel.remoteAddress(), channel.localAddress(), new Date());
            } else if (cause instanceof ReadTimeoutException) {
                System.out.println(channel.isActive());
                LOG.info("{}##通道 {} ==> {} 在 {} 时断开链接", "读取数据超时", channel.remoteAddress(), channel.localAddress(), new Date());
            } else {
                MessageHandler.LOG.warn(this.getClass().getName() + ".exceptionCaught() 截获异常 : ", cause.getCause());
            }
        }
        super.exceptionCaught(ctx, cause);
    }

    @Override
    protected void messageReceived(ChannelHandlerContext context, Message msg) throws Exception {

        Channel channel = context.channel();
        // 客户端发来请求格式无法解析的时候返回-1,并关闭Socket
        if (!channel.isActive())
            return;

        if (msg == null) {
            MessageHandler.LOG.warn("读取的message为null 服务器主动断开 {} 连接", channel.localAddress());
            // channel.write(new SimpleResponse(CoreResponseCode.DECODE_ERROR));
            channel.disconnect();
            return;
        }

        //TODO 客户端没有serverContext

        try {
            Session session = null;
            DispatcherCommand<?> command = null;
            if (msg.getMessage() == MessageType.REQUEST) {
                command = requestCommand(channel, msg);
                session = channel.attr(NetAttributeKey.SERVER_SESSION).get();
            }
            if (msg.getMessage() == MessageType.RESPONSE) {
                command = responseCommand(channel, msg);
                session = channel.attr(NetAttributeKey.CLIENT_SESSION).get();
            }
            if (command != null) {
                this.commandExecutor.submit(session, command);
            }
        } catch (Throwable ex) {
            MessageHandler.LOG.error("#GameServerHandler#接受请求异常", ex);
        }
    }

    private DispatcherCommand<?> requestCommand(Channel channel, Message message) {
        AppContext appContext = channel.attr(NetAttributeKey.CONTEXT).get();
        ServerSession session = channel.attr(NetAttributeKey.SERVER_SESSION).get();
        try {
            if (session == null) {
                session = this.sessionFactory.createSession(channel);
                channel.attr(NetAttributeKey.SESSION).set(session);
                channel.attr(NetAttributeKey.SERVER_SESSION).set(session);
            }
            Request request = (Request) message;
            request.requsetBy(session);
            CoreLogger.log(session, request);
            return this.messageDispatcher.dispatch(request, session, appContext);
        } catch (Throwable ex) {
            if (session != null)
                channel.writeAndFlush(session.getMessageBuilderFactory()
                        .newResponseBuilder()
                        .setProtocol(message.getProtocol())
                        .setResult(CoreResponseCode.RECEIVE_ERROR));
            MessageHandler.LOG.error("#GameServerHandler#接受请求异常", ex);
        }
        return null;
    }

    private DispatcherCommand<?> responseCommand(Channel channel, Message message) {
        AppContext appContext = channel.attr(NetAttributeKey.CONTEXT).get();
        try {
            ClientSession session = channel.attr(NetAttributeKey.CLIENT_SESSION).get();
            ExceptionUtils.checkNotNull(session, "session 为空");
            Response response = (Response) message;
            CoreLogger.log(session, response);
            return this.messageDispatcher.dispatch(response, session, appContext);
        } catch (Throwable ex) {
            MessageHandler.LOG.error("#GameServerHandler#接受请求异常", ex);
        }
        return null;
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        NetSession session = channel.attr(NetAttributeKey.SESSION).get();
        if (session != null) {
            if (this.sessionHolder != null) {
                this.sessionHolder.offline(session);
                this.sessionHolder.disconnect(session);
            }
        }
        if (MessageHandler.LOG.isInfoEnabled()) {
            MessageHandler.LOG.info("断开链接##通道 {} ==> {} 在 {} 时断开链接", channel.remoteAddress(), channel.localAddress(), new Date());
        }
        super.channelUnregistered(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            Channel channel = ctx.channel();
            Session session = channel.attr(NetAttributeKey.SESSION).get();
            if (session != null) {
                String op = "空闲超时";
                switch (event.state()) {
                    case READER_IDLE:
                        op = "读空闲超时";
                        break;
                    case WRITER_IDLE:
                        op = "写空闲超时";
                        break;
                    default:
                        break;
                }
                LOG.info("{}##通道 {} ==> {} 在 {} 时开始断开链接", op, channel.remoteAddress(), channel.localAddress(), new Date());
            }
            ctx.close();
        }
        super.userEventTriggered(ctx, evt);
    }

}

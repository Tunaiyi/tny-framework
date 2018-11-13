package com.tny.game.net.netty4.telnet;

import io.netty.channel.ChannelHandler.Sharable;

/**
 * 游戏请求处理器. 负责获取请求并将请求传给分发器
 *
 * @author KGTny
 */
@Sharable
public class TelnetHandler {// extends SimpleChannelInboundHandler<String> {

    // public static final Logger LOGGER = LoggerFactory.getLogger(TelnetHandler.class);
    //
    // private TelnetCommandHolder telnetCommandHolder;
    //
    // private AtomicInteger uid = new AtomicInteger(0);
    //
    // public TelnetHandler(TelnetCommandHolder telnetCommandHolder) {
    //     this.telnetCommandHolder = telnetCommandHolder;
    // }
    //
    // @Override
    // public void channelActive(ChannelHandlerContext ctx) throws Exception {
    //     Channel channel = ctx.channel();
    //     final Object attachment = channel.attr(NettyAttrKeys.TELNET_SESSION).get();
    //     TelnetSession session = null;
    //     if (attachment == null) {
    //         session = new TelnetSession(this.uid.incrementAndGet(), channel);
    //         channel.attr(NettyAttrKeys.TELNET_SESSION).set(session);
    //     }
    //     this.handleCommand(CommandType.CONNECT, session);
    // }
    //
    // private void handleCommand(CommandType type, TelnetSession session) {
    //     List<TelnetCommand> commandList = this.telnetCommandHolder.getCommandByType(type);
    //     for (TelnetCommand command : commandList) {
    //         session.response(null, command.handlerCommand(session, new TelnetArgument(new String[0])));
    //     }
    //
    // }
    //
    // @Override
    // protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
    //     Channel channel = ctx.channel();
    //     final Object attachment = channel.attr(NettyAttrKeys.TELNET_SESSION).get();
    //     TelnetSession session = null;
    //     if (TelnetSession.class.isInstance(attachment))
    //         session = (TelnetSession) attachment;
    //     String[] commands = msg.trim().split(" ");
    //     TelnetCommand command = this.telnetCommandHolder.getCommand(commands[0].toLowerCase());
    //     if (command == null) {
    //         this.handleCommand(CommandType.DEFAULT, session);
    //     } else {
    //         ChannelFuture future;
    //         try {
    //             future = channel.writeAndFlush(command.handlerCommand(session, new TelnetArgument(commands)) + "\r\n");
    //         } catch (Exception e) {
    //             TelnetHandler.LOGGER.error("", e);
    //             future = channel.writeAndFlush("system error" + "\r\n");
    //         }
    //         if (command.getCommandType() == CommandType.FINISH)
    //             future.addListener(ChannelFutureListener.CLOSE);
    //     }
    //
    // }

}
package com.tny.game.flash;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class FlashPolicyHandler extends SimpleChannelInboundHandler<String> {

    private static final String SECURITY_FILE = "<cross-domain-policy>"
            //+ "<site-control permitted-cross-domain-policies=\"all\"/>"
            + "<allow-access-from domain=\"*\" to-ports=\"*\" />"
            + "</cross-domain-policy>" + "\0";


    @Override
    protected void messageReceived(ChannelHandlerContext ctx, String message) throws Exception {
        if (message.contains("<policy-file-request/>")) {
            ChannelFuture future = ctx.channel().writeAndFlush(SECURITY_FILE);
            future.addListener((ChannelFuture f) -> ctx.channel().disconnect());
            ctx.flush();
        }
    }
}

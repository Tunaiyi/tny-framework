package com.tny.game.net.netty4;

import com.tny.game.net.base.configuration.CommonAppConfiguration;

import java.io.IOException;
import java.util.List;

public class NettyAppConfiguration<T> extends CommonAppConfiguration<T> {

    public NettyAppConfiguration(String name, T defaultUserId) {
        super(name, defaultUserId);
    }

    public NettyAppConfiguration(String name, T defaultUserId, String path) throws IOException {
        super(name, defaultUserId, path);
    }

    public NettyAppConfiguration(String name, T defaultUserId, List<String> paths) throws IOException {
        super(name, defaultUserId, paths);
    }

}

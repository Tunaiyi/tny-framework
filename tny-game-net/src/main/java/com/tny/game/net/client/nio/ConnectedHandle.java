package com.tny.game.net.client.nio;

import com.tny.game.net.client.exception.ClientException;


public interface ConnectedHandle {

    public void connected(NetClient client) throws ClientException;

}

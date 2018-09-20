package com.tny.game.net.transport;

import com.tny.game.net.transport.message.Message;

import java.util.*;

/**
 * Created by Kun Yang on 2018/8/23.
 */
public class ResendResult<UID> {

    private Tunnel<UID> tunnel;

    private List<Message<UID>> messages;

    public ResendResult(Tunnel<UID> tunnel, List<Message<UID>> messages) {
        this.tunnel = tunnel;
        this.messages = Collections.unmodifiableList(messages);
    }

    public Tunnel<UID> getTunnel() {
        return tunnel;
    }

    public List<Message<UID>> getMessages() {
        return messages;
    }
}

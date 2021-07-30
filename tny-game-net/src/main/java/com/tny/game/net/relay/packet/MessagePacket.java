package com.tny.game.net.relay.packet;

import com.tny.game.net.message.*;
import com.tny.game.net.relay.*;

/**
 * 传输事件
 * <p>
 *
 * @author : kgtny
 * @date : 2021/2/26 5:14 上午
 */
public class MessagePacket extends BaseRelayPacket {

    private final Message message;

    public MessagePacket(long id, Message message) {
        super(id);
        this.message = message;
    }

    public MessagePacket(long id, long nanoTime, Message message) {
        super(id, nanoTime);
        this.message = message;
    }

    public MessagePacket(Tubule<?> tubule, Message message) {
        super(tubule);
        this.message = message;
    }

    public MessagePacket(Tubule<?> tubule, long nanoTime, Message message) {
        super(tubule, nanoTime);
        this.message = message;
    }

    public Message getMessage() {
        return this.message;
    }

    @Override
    public RelayPacketType getType() {
        return RelayPacketType.MESSAGE;
    }

}
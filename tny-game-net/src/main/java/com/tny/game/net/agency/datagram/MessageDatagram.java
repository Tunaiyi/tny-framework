package com.tny.game.net.agency.datagram;

import com.tny.game.net.agency.*;
import com.tny.game.net.message.*;

/**
 * 传输事件
 * <p>
 *
 * @author : kgtny
 * @date : 2021/2/26 5:14 上午
 */
public class MessageDatagram extends BaseTubuleDatagram {

    private final Message message;

    public MessageDatagram(long id, Message message) {
        super(id);
        this.message = message;
    }

    public MessageDatagram(long id, long nanoTime, Message message) {
        super(id, nanoTime);
        this.message = message;
    }

    public MessageDatagram(Tubule<?> tubule, Message message) {
        super(tubule);
        this.message = message;
    }

    public MessageDatagram(Tubule<?> tubule, long nanoTime, Message message) {
        super(tubule, nanoTime);
        this.message = message;
    }

    public Message getMessage() {
        return this.message;
    }

    @Override
    public AgentDatagramHandlerType getType() {
        return AgentDatagramHandlerType.MESSAGE;
    }

}
package com.tny.game.net.transport;

import org.junit.*;

import static org.junit.Assert.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-19 14:16
 */
public class MessageIdCreatorTest {

    private MessageIdCreator oneCreator = new MessageIdCreator(MessageIdCreator.TUNNEL_SENDER_MESSAGE_ID_MARK);
    private MessageIdCreator otherCreator = new MessageIdCreator(MessageIdCreator.ENDPOINT_SENDER_MESSAGE_ID_MARK);

    @Test
    public void isCreate() {
        for (int index = 0; index < 10000; index++) {
            long oneId = oneCreator.createId();
            assertTrue(oneCreator.isCreate(oneId));
            assertFalse(otherCreator.isCreate(oneId));
            long otherId = otherCreator.createId();
            assertFalse(oneCreator.isCreate(otherId));
            assertTrue(otherCreator.isCreate(otherId));
        }
    }

}
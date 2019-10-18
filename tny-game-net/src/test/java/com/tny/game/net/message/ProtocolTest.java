package com.tny.game.net.message;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Created by Kun Yang on 2018/8/23.
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class ProtocolTest {

    @Mock
    public Message<Long> message;

    @Mock
    public MessageHead header;

    private int protocolId;

    protected ProtocolTest(int protocolId) {
        this.protocolId = protocolId;
    }

    @Before
    public void setUp() throws Exception {

    }

    protected abstract Protocol protocol();

    @Test
    public void getProtocol() {
        Protocol protocol = protocol();
        assertEquals(protocolId, protocol.getProtocolNumber());
    }

    @Test
    public void isOwn() {
        Protocol protocol = protocol();
        when(message.getHead()).thenReturn(header);
        when(header.getProtocolNumber()).thenReturn(-1000);
        assertFalse(protocol.isOwn(message));
        when(message.getHead()).thenReturn(header);
        when(message.getProtocolNumber()).thenReturn(protocol.getProtocolNumber());
        assertTrue(protocol.isOwn(message));
    }

    @Test
    public void isOwn1() {
        Protocol protocol = protocol();
        when(header.getProtocolNumber()).thenReturn(-1000);
        assertFalse(protocol.isOwn(header));
        when(header.getProtocolNumber()).thenReturn(protocol.getProtocolNumber());
        assertTrue(protocol.isOwn(header));
    }

}
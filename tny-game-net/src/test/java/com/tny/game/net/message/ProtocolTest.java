package com.tny.game.net.message;

import org.junit.jupiter.api.*;
import org.junit.runner.*;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Created by Kun Yang on 2018/8/23.
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class ProtocolTest {

    @Mock
    public Message message;

    @Mock
    public MessageHead header;

    private int protocolId;

    protected ProtocolTest(int protocolId) {
        this.protocolId = protocolId;
    }

    @BeforeEach
    public void setUp() throws Exception {

    }

    protected abstract Protocol protocol();

    @Test
    public void getProtocol() {
        Protocol protocol = protocol();
        assertEquals(this.protocolId, protocol.getProtocolId());
    }

    @Test
    public void isOwn() {
        Protocol protocol = protocol();
        when(this.message.getHead()).thenReturn(this.header);
        when(this.header.getProtocolId()).thenReturn(-1000);
        assertFalse(protocol.isOwn((MessageContent)this.message));
        when(this.message.getHead()).thenReturn(this.header);
        when(this.message.getProtocolId()).thenReturn(protocol.getProtocolId());
        assertTrue(protocol.isOwn((MessageContent)this.message));
    }

    @Test
    public void isOwn1() {
        Protocol protocol = protocol();
        when(this.header.getProtocolId()).thenReturn(-1000);
        assertFalse(protocol.isOwn(this.header));
        when(this.header.getProtocolId()).thenReturn(protocol.getProtocolId());
        assertTrue(protocol.isOwn(this.header));
    }

}
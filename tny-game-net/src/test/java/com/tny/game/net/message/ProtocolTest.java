/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.tny.game.net.message;

import org.junit.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.junit.*;

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

    private final int protocolId;

    protected ProtocolTest(int protocolId) {
        this.protocolId = protocolId;
    }

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
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
        when(this.message.getProtocolId()).thenReturn(-1000);
        assertFalse(protocol.isOwn(this.message));
        when(this.message.getHead()).thenReturn(this.header);
        when(this.message.getProtocolId()).thenReturn(protocol.getProtocolId());
        assertTrue(protocol.isOwn((MessageSubject)this.message));
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
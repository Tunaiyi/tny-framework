/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tny.game.net.message;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Created by Kun Yang on 2018/8/23.
 */
@ExtendWith(MockitoExtension.class)
public abstract class ProtocolTest {

    @Mock
    public Message message;

    @Mock
    public MessageHead header;

    private final int protocolId;

    protected ProtocolTest(int protocolId) {
        this.protocolId = protocolId;
    }

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
        when(this.message.getProtocolId()).thenReturn(-1000);
        assertFalse(protocol.isOwn(this.message));
        when(this.message.getProtocolId()).thenReturn(protocol.getProtocolId());
        assertTrue(protocol.isOwn(this.message));
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
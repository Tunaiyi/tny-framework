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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Kun Yang on 2017/3/26.
 */
public abstract class MessageTest {

    protected final int MESSAGE_ID = 100;

    protected final Long UID = 100L;

    protected Long unloginUID = 0L;

    protected Message message = message();

    protected Message unloginMessage = unloginMessage(this.unloginUID);

    protected Message unloginNullMessage = unloginMessage(null);

    protected abstract Message message();

    protected abstract Message unloginMessage(Long unloginID);

    @Test
    public void getId() throws Exception {
        Message message = message();
        assertEquals(this.MESSAGE_ID, message.getId());
    }

    //    @Test
    //    public void getUserId() throws Exception {
    //        assertEquals(this.UID, this.message.getUserId());
    //        assertEquals(this.unloginUID, this.unloginMessage.getUserId());
    //        assertNull(this.unloginNullMessage.getUserId());
    //    }

    //    @Test
    //    public void getContactGroup() throws Exception {
    //        assertEquals(Certificates.DEFAULT_USER_TYPE, this.message.getUserType());
    //        assertEquals(Certificates.ANONYMITY_USER_TYPE, this.unloginMessage.getUserType());
    //        assertEquals(Certificates.ANONYMITY_USER_TYPE, this.unloginNullMessage.getUserType());
    //    }

    @Test
    public void getCode() throws Exception {

    }

    @Test
    public void getToMessage() throws Exception {
    }

    @Test
    public void getBody() throws Exception {
    }

    @Test
    public void getTime() throws Exception {
    }

    @Test
    public void getSign() throws Exception {
    }

    @Test
    public void attributes() throws Exception {
    }

    @Test
    public void getMode() throws Exception {
    }

}
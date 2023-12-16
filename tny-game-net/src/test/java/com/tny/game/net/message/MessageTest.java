/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
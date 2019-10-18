package com.tny.game.net.message;


import com.tny.game.net.transport.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by Kun Yang on 2017/3/26.
 */
public abstract class MessageTest {

    protected final int MESSAGE_ID = 100;
    protected final Long UID = 100L;
    protected Long unloginUID = 0L;


    protected Message<Long> message = message();
    protected Message<Long> unloginMessage = unloginMessage(unloginUID);
    protected Message<Long> unloginNullMessage = unloginMessage(null);

    protected abstract Message<Long> message();

    protected abstract Message<Long> unloginMessage(Long unloginID);

    @Test
    public void getId() throws Exception {
        Message<Long> message = message();
        assertEquals(MESSAGE_ID, message.getId());
    }

    @Test
    public void getUserId() throws Exception {
        assertEquals(UID, message.getUserId());
        assertEquals(unloginUID, unloginMessage.getUserId());
        assertNull(unloginNullMessage.getUserId());
    }

    @Test
    public void getUserGroup() throws Exception {
        assertEquals(Certificates.DEFAULT_USER_TYPE, message.getUserType());
        assertEquals(Certificates.UNLOGIN_USER_TYPE, unloginMessage.getUserType());
        assertEquals(Certificates.UNLOGIN_USER_TYPE, unloginNullMessage.getUserType());
    }

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
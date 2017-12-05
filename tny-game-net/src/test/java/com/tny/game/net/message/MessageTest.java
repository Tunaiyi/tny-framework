package com.tny.game.net.message;


import com.tny.game.net.utils.NetConfigs;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Kun Yang on 2017/3/26.
 */
public abstract class MessageTest {

    protected final int MESSAGE_ID = 100;
    protected final Long UID = 100L;
    protected Long unloginUID = 0L;


    protected Message<Long> message = message();
    protected Message<Long> unlogin0Message = unloginMessage(unloginUID);
    protected Message<Long> unloginNullMessage = unloginMessage(null);

    protected abstract Message<Long> message();

    protected abstract Message<Long> unloginMessage(Long unloginID);

    @Test
    public void getID() throws Exception {
        Message<Long> message = message();
        assertEquals(MESSAGE_ID, message.getID());
    }

    @Test
    public void getUserID() throws Exception {
        assertEquals(UID, message.getUserID());
        assertEquals(unloginUID, unlogin0Message.getUserID());
        assertNull(unloginNullMessage.getUserID());
    }

    @Test
    public void getUserGroup() throws Exception {
        assertEquals(NetConfigs.DEFAULT_USER_GROUP, message.getUserGroup());
        assertEquals(NetConfigs.UNLOGIN_USER_GROUP, unlogin0Message.getUserGroup());
        assertEquals(NetConfigs.UNLOGIN_USER_GROUP, unloginNullMessage.getUserGroup());
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
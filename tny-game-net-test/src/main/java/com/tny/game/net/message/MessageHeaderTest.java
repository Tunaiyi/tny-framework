package com.tny.game.net.message;

import com.tny.game.common.result.ResultCode;
import com.tny.game.common.utils.*;
import org.junit.*;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by Kun Yang on 2018/8/20.
 */
public abstract class MessageHeaderTest extends ProtocolTest {

    private long id = 10L;
    private long toMessage = 8L;
    public static final int protocol = 100;

    private static long now = System.currentTimeMillis();
    private String head = "I'm message head";
    private List<String> listHead = Arrays.asList("I'm message head", "I'm message head", "I'm message head", "I'm message head");

    private static ReferenceType<List<String>> LIST_HEAD_TYPE = new ReferenceType<List<String>>() {
    };

    private static ReferenceType<Map<String, String>> MAP_HEAD_TYPE = new ReferenceType<Map<String, String>>() {
    };

    private MessageHeader REQUEST_HEAD = create(id, protocol, ResultCode.SUCCESS_CODE, now, 0, head);
    private MessageHeader SUCCESS_RESPONSE_HEAD = create(id, protocol, ResultCode.SUCCESS_CODE, now, toMessage, listHead);
    private MessageHeader FAIL_RESPONSE_HEAD = create(id, protocol, ResultCode.FAILURE_CODE, now, toMessage, null);
    private MessageHeader PUSH_HEAD = create(id, protocol, ResultCode.SUCCESS_CODE, now, MessageAide.PUSH_TO_MESSAGE_ID, null);

    public MessageHeaderTest() {
        super(protocol);
    }

    @Override
    protected Protocol protocol() {
        return create(id, protocol, ResultCode.SUCCESS_CODE, now, 0, head);
    }

    public abstract MessageHeader create(long id, int protocol, int code, long time, long toMessage, Object head);

    @Test
    public void getId() {
        assertEquals(REQUEST_HEAD.getId(), id);
        assertEquals(SUCCESS_RESPONSE_HEAD.getId(), id);
        assertEquals(FAIL_RESPONSE_HEAD.getId(), id);
        assertEquals(PUSH_HEAD.getId(), id);
    }

    @Test
    public void getCode() {
        assertEquals(REQUEST_HEAD.getCode(), ResultCode.SUCCESS_CODE);
        assertEquals(SUCCESS_RESPONSE_HEAD.getCode(), ResultCode.SUCCESS_CODE);
        assertEquals(FAIL_RESPONSE_HEAD.getCode(), ResultCode.FAILURE_CODE);
        assertEquals(PUSH_HEAD.getCode(), ResultCode.SUCCESS_CODE);
    }

    @Test
    public void getTime() {
        assertEquals(REQUEST_HEAD.getTime(), now);
        assertEquals(SUCCESS_RESPONSE_HEAD.getTime(), now);
        assertEquals(FAIL_RESPONSE_HEAD.getTime(), now);
        assertEquals(PUSH_HEAD.getTime(), now);
    }

    @Test
    public void getToMessage() {
        assertEquals(REQUEST_HEAD.getToMessage(), MessageAide.REQUEST_TO_MESSAGE_ID);
        assertEquals(SUCCESS_RESPONSE_HEAD.getToMessage(), toMessage);
        assertEquals(FAIL_RESPONSE_HEAD.getToMessage(), toMessage);
        assertEquals(PUSH_HEAD.getToMessage(), MessageAide.PUSH_TO_MESSAGE_ID);
    }

    @Test
    public void getMode() {
        assertEquals(REQUEST_HEAD.getMode(), MessageMode.REQUEST);
        assertEquals(SUCCESS_RESPONSE_HEAD.getMode(), MessageMode.RESPONSE);
        assertEquals(FAIL_RESPONSE_HEAD.getMode(), MessageMode.RESPONSE);
        assertEquals(PUSH_HEAD.getMode(), MessageMode.PUSH);
    }

    @Test
    public void isHasHead() {
        assertTrue(REQUEST_HEAD.isHasAttachment());
        assertTrue(SUCCESS_RESPONSE_HEAD.isHasAttachment());
        assertFalse(FAIL_RESPONSE_HEAD.isHasAttachment());
        assertFalse(PUSH_HEAD.isHasAttachment());
    }

    @Test
    public void getHead() {
        assertEquals(REQUEST_HEAD.getAttachment(String.class), head);
        try {
            REQUEST_HEAD.getAttachment(Map.class);
            fail(StringAide.format("获取 head {} 类型错误没有抛出异常", Map.class));
        } catch (Exception e) {
            assertTrue(true);
        }
        assertEquals(SUCCESS_RESPONSE_HEAD.getAttachment(LIST_HEAD_TYPE), listHead);
        try {
            SUCCESS_RESPONSE_HEAD.getAttachment(MAP_HEAD_TYPE);
            fail(StringAide.format("获取 head {} 类型错误没有抛出异常", MAP_HEAD_TYPE));
        } catch (Exception e) {
            assertTrue(true);
        }
        assertNull(FAIL_RESPONSE_HEAD.getAttachment(String.class));
        assertNull(PUSH_HEAD.getAttachment(String.class));
    }

}
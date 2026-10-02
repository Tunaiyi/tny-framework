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

import com.tny.game.common.result.*;
import com.tny.game.common.type.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Kun Yang on 2018/8/20.
 */
public abstract class MessageHeadTest extends ProtocolTest {

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

    private MessageHead REQUEST_HEAD = create(this.id, MessageMode.REQUEST, protocol, ResultCode.SUCCESS_CODE, now, 0);

    private MessageHead SUCCESS_RESPONSE_HEAD = create(this.id, MessageMode.RESPONSE, protocol, ResultCode.SUCCESS_CODE, now, this.toMessage);

    private MessageHead FAIL_RESPONSE_HEAD = create(this.id, MessageMode.RESPONSE, protocol, ResultCode.FAILURE_CODE, now, this.toMessage);

    private MessageHead PUSH_HEAD = create(this.id, MessageMode.PUSH, protocol, ResultCode.SUCCESS_CODE, now, MessageConstants.EMPTY_MESSAGE_ID);

    public MessageHeadTest() {
        super(protocol);
    }

    @Override
    protected Protocol protocol() {
        return create(this.id, MessageMode.REQUEST, protocol, ResultCode.SUCCESS_CODE, now, 0);
    }

    public abstract MessageHead create(long id, MessageMode mode, int protocol, int code, long time, long toMessage);

    @Test
    public void getId() {
        assertEquals(this.REQUEST_HEAD.getId(), this.id);
        assertEquals(this.SUCCESS_RESPONSE_HEAD.getId(), this.id);
        assertEquals(this.FAIL_RESPONSE_HEAD.getId(), this.id);
        assertEquals(this.PUSH_HEAD.getId(), this.id);
    }

    @Test
    public void getCode() {
        assertEquals(this.REQUEST_HEAD.getCode(), ResultCode.SUCCESS_CODE);
        assertEquals(this.SUCCESS_RESPONSE_HEAD.getCode(), ResultCode.SUCCESS_CODE);
        assertEquals(this.FAIL_RESPONSE_HEAD.getCode(), ResultCode.FAILURE_CODE);
        assertEquals(this.PUSH_HEAD.getCode(), ResultCode.SUCCESS_CODE);
    }

    @Test
    public void getTime() {
        assertEquals(this.REQUEST_HEAD.getTime(), now);
        assertEquals(this.SUCCESS_RESPONSE_HEAD.getTime(), now);
        assertEquals(this.FAIL_RESPONSE_HEAD.getTime(), now);
        assertEquals(this.PUSH_HEAD.getTime(), now);
    }

    @Test
    public void getToMessage() {
        assertEquals(this.REQUEST_HEAD.getToMessage(), MessageConstants.EMPTY_MESSAGE_ID);
        assertEquals(this.SUCCESS_RESPONSE_HEAD.getToMessage(), this.toMessage);
        assertEquals(this.FAIL_RESPONSE_HEAD.getToMessage(), this.toMessage);
        assertEquals(this.PUSH_HEAD.getToMessage(), MessageConstants.EMPTY_MESSAGE_ID);
    }

    @Test
    public void getMode() {
        assertEquals(this.REQUEST_HEAD.getMode(), MessageMode.REQUEST);
        assertEquals(this.SUCCESS_RESPONSE_HEAD.getMode(), MessageMode.RESPONSE);
        assertEquals(this.FAIL_RESPONSE_HEAD.getMode(), MessageMode.RESPONSE);
        assertEquals(this.PUSH_HEAD.getMode(), MessageMode.PUSH);
    }

    // @Test
    // public void isHasHead() {
    //     assertTrue(REQUEST_HEAD.isHasAttachment());
    //     assertTrue(SUCCESS_RESPONSE_HEAD.isHasAttachment());
    //     assertFalse(FAIL_RESPONSE_HEAD.isHasAttachment());
    //     assertFalse(PUSH_HEAD.isHasAttachment());
    // }

    // @Test
    // public void getHead() {
    //     assertEquals(REQUEST_HEAD.getAttachment(String.class), head);
    //     try {
    //         REQUEST_HEAD.getAttachment(Map.class);
    //         fail(StringAide.format("获取 head {} 类型错误没有抛出异常", Map.class));
    //     } catch (Exception e) {
    //         assertTrue(true);
    //     }
    //     assertEquals(SUCCESS_RESPONSE_HEAD.getAttachment(LIST_HEAD_TYPE), listHead);
    //     try {
    //         SUCCESS_RESPONSE_HEAD.getAttachment(MAP_HEAD_TYPE);
    //         fail(StringAide.format("获取 head {} 类型错误没有抛出异常", MAP_HEAD_TYPE));
    //     } catch (Exception e) {
    //         assertTrue(true);
    //     }
    //     assertNull(FAIL_RESPONSE_HEAD.getAttachment(String.class));
    //     assertNull(PUSH_HEAD.getAttachment(String.class));
    // }

}
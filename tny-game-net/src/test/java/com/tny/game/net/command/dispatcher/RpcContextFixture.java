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
package com.tny.game.net.command.dispatcher;

import com.tny.game.common.context.AttributeHolder;
import com.tny.game.common.type.ReferenceType;
import com.tny.game.net.command.plugins.CommandPlugin;
import com.tny.game.net.message.*;

import java.lang.reflect.Field;

/**
 * 测试支撑：绕过 javassist/注解/表达式装配链，直接构造可断言的 RpcInvokeContext 与消息桩。
 * 仅供 command/plugins 与 command/dispatcher 的检查器行为测试使用。
 */
public final class RpcContextFixture {

    private static final sun.misc.Unsafe UNSAFE = unsafe();

    private RpcContextFixture() {
    }

    public static RpcInvokeContext context() {
        try {
            MethodControllerHolder controller = (MethodControllerHolder) UNSAFE.allocateInstance(MethodControllerHolder.class);
            setField(controller, MethodControllerHolder.class, "simpleName", "fixture.probe");
            RpcInvokeContext context = (RpcInvokeContext) UNSAFE.allocateInstance(RpcInvokeContext.class);
            setField(context, RpcInvokeContext.class, "controller", controller);
            setField(context, RpcInvokeContext.class, "promise", new MessageCommandPromise("fixture.probe", 3000));
            return context;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static CommandPluginHolder holder(CommandPlugin<?> plugin) {
        try {
            CommandPluginHolder holder = (CommandPluginHolder) UNSAFE.allocateInstance(CommandPluginHolder.class);
            setField(holder, CommandPluginHolder.class, "plugin", plugin);
            setField(holder, CommandPluginHolder.class, "attributes", null);
            return holder;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static StubMessage message(long id, long time) {
        return new StubMessage(id, time);
    }

    private static void setField(Object target, Class<?> owner, String name, Object value) throws Exception {
        Field field = owner.getDeclaredField(name);
        field.setAccessible(true);
        if (value instanceof Boolean b) {
            field.setBoolean(target, b);
        } else if (value == null) {
            field.set(target, null);
        } else {
            field.set(target, value);
        }
    }

    /**
     * 消息桩：id/time 可指定，其余为惰性默认值（检查器插件仅读取 head 的 id/time）。
     */
    public static final class StubMessage extends AttributeHolder implements NetMessage {
        private final long id;
        private final long time;

        StubMessage(long id, long time) {
            this.id = id;
            this.time = time;
        }

        @Override
        public MessageHead getHead() {
            return this;
        }

        @Override
        public long getId() {
            return this.id;
        }

        @Override
        public long getTime() {
            return this.time;
        }

        @Override
        public long getToMessage() {
            return 0L;
        }

        @Override
        public int getCode() {
            return 0;
        }

        @Override
        public int getProtocolId() {
            return 1000;
        }

        @Override
        public MessageMode getMode() {
            return MessageMode.REQUEST;
        }

        @Override
        public <T extends MessageHeader<?>> T getHeader(String key, Class<T> headerClass) {
            return null;
        }

        @Override
        public <T extends MessageHeader<?>> T getHeader(MessageHeaderKey<T> key) {
            return null;
        }

        @Override
        public <T extends MessageHeader<?>> java.util.List<T> getHeaders(Class<T> headerClass) {
            return java.util.List.of();
        }

        @Override
        public boolean isHasHeaders() {
            return false;
        }

        @Override
        public java.util.List<MessageHeader<?>> getAllHeaders() {
            return java.util.List.of();
        }

        @Override
        public java.util.Map<String, MessageHeader<?>> getAllHeaderMap() {
            return java.util.Map.of();
        }

        @Override
        public boolean existHeader(String key) {
            return false;
        }

        @Override
        public boolean existHeader(MessageHeaderKey<?> key) {
            return false;
        }

        @Override
        public boolean existHeader(String key, Class<? extends MessageHeader<?>> headerClass) {
            return false;
        }

        @Override
        public <H extends MessageHeader<H>> MessageHeader<H> putHeader(MessageHeader<H> header) {
            return header;
        }

        @Override
        public <H extends MessageHeader<H>> MessageHeader<H> putHeaderIfAbsent(MessageHeader<H> header) {
            return header;
        }

        @Override
        public <T extends MessageHeader<?>> T removeHeader(String key) {
            return null;
        }

        @Override
        public <T extends MessageHeader<?>> T removeHeader(String key, Class<T> headerClass) {
            return null;
        }

        @Override
        public <T extends MessageHeader<?>> T removeHeader(MessageHeaderKey<T> key) {
            return null;
        }

        @Override
        public void removeHeaders(Iterable<String> keys) {
        }

        @Override
        public void removeAllHeaders() {
        }

        @Override
        public boolean existBody() {
            return false;
        }

        @Override
        public Object getBody() {
            return null;
        }

        @Override
        public <T> T bodyAs(Class<T> clazz) {
            return null;
        }

        @Override
        public <T> T bodyAs(ReferenceType<T> clazz) {
            return null;
        }

        @Override
        public void allotMessageId(long id) {
        }

        @Override
        public boolean isRelay() {
            return false;
        }

        @Override
        public void relay(boolean value) {
        }
    }

    private static sun.misc.Unsafe unsafe() {
        try {
            Field field = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return (sun.misc.Unsafe) field.get(null);
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

}

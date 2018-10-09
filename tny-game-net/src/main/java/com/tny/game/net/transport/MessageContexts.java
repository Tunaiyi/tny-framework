package com.tny.game.net.transport;

import com.google.common.base.MoreObjects;
import com.tny.game.common.concurrent.StageableFuture;
import com.tny.game.net.transport.message.Message;

import java.util.function.Consumer;

/**
 * 消息上下文
 * Created by Kun Yang on 2017/2/16.
 */
public class MessageContexts {

    public static <UID> MessageContext<UID> createContext() {
        return new DefaultMessageContent<>();
    }

    private static class DefaultMessageContent<UID> implements MessageContext<UID> {

        /**
         * 发送消息 Future
         */
        private volatile MessageSendFuture<UID> sendFuture;

        /**
         * 收到响应消息 Future, 只有 mode 为  request 才可以是使用
         */
        private volatile RespondFuture<UID> respondFuture;

        private DefaultMessageContent() {
        }

        @Override
        public MessageContext<UID> willSendFuture(Consumer<MessageSendFuture<UID>> consumer) {
            consumer.accept(this.loadOrCreateSendFuture());
            return this;
        }

        @Override
        public MessageContext<UID> willSendFuture() {
            this.loadOrCreateSendFuture();
            return this;
        }

        @Override
        public MessageContext<UID> willResponseFuture(long lifeTime, Consumer<StageableFuture<Message<UID>>> consumer) {
            consumer.accept(this.loadOrCreateRespondFuture(lifeTime));
            return this;
        }

        @Override
        public MessageContext<UID> willResponseFuture(long lifeTime) {
            this.loadOrCreateRespondFuture(lifeTime);
            return this;
        }

        private RespondFuture<UID> loadOrCreateRespondFuture(long lifeTime) {
            if (this.respondFuture != null)
                return this.respondFuture;
            synchronized (this) {
                if (this.respondFuture == null)
                    this.respondFuture = new RespondFuture<>(lifeTime);
            }
            return this.respondFuture;
        }

        private MessageSendFuture<UID> loadOrCreateSendFuture() {
            if (this.sendFuture != null)
                return this.sendFuture;
            synchronized (this) {
                if (this.sendFuture == null)
                    this.sendFuture = new MessageSendFuture<>();
            }
            return this.sendFuture;
        }

        @Override
        public boolean isHasSendFuture() {
            return this.sendFuture != null;
        }

        @Override
        public boolean isHasRespondFuture() {
            return this.respondFuture != null;
        }

        @Override
        public MessageSendFuture<UID> getSendFuture() {
            return sendFuture;
        }

        @Override
        public RespondFuture<UID> getRespondFuture() {
            return respondFuture;
        }

        @Override
        public boolean isHasFuture() {
            return this.sendFuture != null || this.respondFuture != null;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .toString();
        }

    }
}

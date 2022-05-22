package com.tny.game.net.base;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.message.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * 默认Messager工厂
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/22 05:16
 **/
@Unit
public class DefaultMessagerFactory implements MessagerFactory {

    @Override
    public <M extends Messager> M createMessager(MessagerType type, long messagerId) {
        return as(new DefaultMessager(type, messagerId));
    }

    @Override
    public <M extends Messager> M createMessager(ForwardMessager messager) {
        return as(messager);
    }

    private static final class DefaultMessager implements Messager {

        private final long messagerId;

        private final MessagerType messagerType;

        private DefaultMessager(MessagerType messagerType, long messagerId) {
            this.messagerType = messagerType;
            this.messagerId = messagerId;
        }

        @Override
        public long getMessagerId() {
            return messagerId;
        }

        @Override
        public MessagerType getMessagerType() {
            return messagerType;
        }

    }

}

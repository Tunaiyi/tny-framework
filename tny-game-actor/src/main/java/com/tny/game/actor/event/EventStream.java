package com.tny.game.actor.event;

import com.tny.game.actor.ActorRef;
import com.tny.game.actor.util.Subclassification;

/**
 * 事件流
 * Created by Kun Yang on 16/1/17.
 */
public class EventStream extends LoggingBus<Object> {

    private Subclassification<Class<?>> subclassification = new Subclassification<Class<?>>() {
        @Override
        public boolean isEqual(Class<?> x, Class<?> y) {
            return x == y;
        }

        @Override
        public boolean isSubclass(Class<?> x, Class<?> y) {
            return y.isAssignableFrom(x);
        }
    };

    @Override
    public boolean subscribe(ActorRef subscriber, Class<?> to) {
        return false;
    }

    @Override
    public boolean unsubscribe(ActorRef subscriber, Class<?> from) {
        return false;
    }

    @Override
    public void unsubscribe(ActorRef subscriber) {

    }

    @Override
    public void publish(Object event) {

    }
}

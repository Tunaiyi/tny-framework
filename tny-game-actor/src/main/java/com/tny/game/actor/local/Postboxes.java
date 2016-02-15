package com.tny.game.actor.local;

import com.tny.game.LogUtils;
import com.tny.game.actor.*;
import com.tny.game.actor.event.EventStream;
import com.tny.game.actor.exception.ConfigurationException;
import com.tny.game.actor.local.message.DeadLetterMsg;
import com.tny.game.actor.local.queue.UnboundedDequeBasedMessageQueue;
import com.tny.game.actor.system.Setting;

/**
 * PostBox的管理气
 *
 * @author KGTny
 */
public class Postboxes {

    private Setting settings;
    private EventStream eventStream;
    private ActorRef deadLetters;

    private Postbox deadLetterPostbox;

    private PostboxFactoryHolder factoryHolder;

    private DefaultPostboxFactory defaultPostboxFactory;

    public Postboxes(Setting settings, PostboxFactoryHolder factoryHolder, EventStream eventStream, ActorRef deadLetters, DefaultPostboxFactory defaultPostboxFactory) {
        this.settings = settings;
        this.eventStream = eventStream;
        this.deadLetters = deadLetters;
        this.defaultPostboxFactory = new DefaultPostboxFactory();
        this.deadLetterPostbox = new DeadLetterPostbox();
        this.factoryHolder = factoryHolder;
    }

    public Postbox getDeadLetterPostbox() {
        return deadLetterPostbox;
    }

    private void save() {

    }

    public PostboxFactory lookupClass(Class<? extends PostboxFactory> clazz) {
        if (clazz == null)
            return defaultPostboxFactory;
        PostboxFactory factory = factoryHolder.getFactoryByClass(clazz);
        if (factory == null) {
            throw new ConfigurationException(
                    LogUtils.format("没有找到 {} 的 PostboxFactory", clazz));
        }
        return factory;
    }

    public PostboxFactory lookupName(String factoryName) {
        if (factoryName == null)
            return defaultPostboxFactory;
        PostboxFactory factory = factoryHolder.getFactoryByName(factoryName);
        if (factory == null) {
            throw new ConfigurationException(
                    LogUtils.format("没有找到名字为 {} 的 PostboxFactory", factoryName));
        }
        return factory;
    }

    private class DeadLetterPostbox extends DefaultPostbox {

        private DeadLetterPostbox() {
            super(new DeadLetterMessageQueue());
            this.close();
        }

        @Override
        public void systemEnqueue(ActorRef receiver, SystemMessage message) {
            deadLetters.tell(new DeadLetterMsg(receiver, message, receiver));
        }

        @Override
        public boolean hasSystemMessages() {
            return false;
        }

    }

    private class DeadLetterMessageQueue implements MessageQueue {

        private DeadLetterMessageQueue() {
        }

        @Override
        public void enqueueFirst(ActorRef receiver, Envelope envelope) {
            if (DeadLetterMsg.class.isInstance(envelope.getMessage())) {
                deadLetters.tell(new DeadLetterMsg(receiver, envelope.getMessage(), receiver));
            }
        }

        @Override
        public void enqueue(ActorRef receiver, Envelope envelope) {
            if (DeadLetterMsg.class.isInstance(envelope.getMessage())) {
                deadLetters.tell(new DeadLetterMsg(receiver, envelope.getMessage(), receiver));
            }
        }

        @Override
        public Envelope dequeue() {
            return null;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean hasMessages() {
            return false;
        }

        @Override
        public void cleanUp(ActorRef owner, MessageQueue deadLetters) {

        }

    }

    private class DefaultPostboxFactory implements PostboxFactory {

        @Override
        public String getName() {
            return "default-postbox";
        }

        @Override
        public Postbox createPostbox(ActorRef actor, ActorSystem system) {
            return new DefaultPostbox(new UnboundedDequeBasedMessageQueue());
        }

    }
}

package com.tny.game.actor.local;

import com.tny.game.actor.*;

import java.util.Iterator;

public class LocalActorRef extends InternalActorRef {

    private ActorCell actorCell;

    protected LocalActorRef(ActorPath path) {
        super(path);
    }

    @Override
    public boolean isLocal() {
        return true;
    }

    protected ActorCell getActorCell() {
        return actorCell;
    }

    @Override
    public void start() {
        actorCell.start();
    }

    @Override
    public void resume(Throwable causedByFailure) {
        actorCell.resume(causedByFailure);
    }

    @Override
    public void suspend() {
        actorCell.suspend();
    }

    @Override
    public void restart(Throwable caused) {
        actorCell.restart(caused);
    }

    @Override
    public void stop() {
        actorCell.stop();
    }

    @Override
    public void tell(Object message) {
        actorCell.sendMessage(message, ActorUtils.noSender(), null);
    }

    @Override
    public void tell(Object message, ActorRef sender) {
        actorCell.sendMessage(message, ActorUtils.orNoSender(sender), null);
    }

    @Override
    public <V> Answer<V> ask(Object message, ActorRef sender, Answer<V> answer) {
        return actorCell.sendMessage(message, ActorUtils.orNoSender(sender), answer);
    }

    @Override
    public void sendSystemMessage(SystemMessage message) {
        actorCell.sendSystemMessage(message);
    }

    @Override
    public ActorRefProvider getProvider() {
        return actorCell.getProvider();
    }

    @Override
    public InternalActorRef getParent() {
        return actorCell.getParent();
    }

    @Override
    public InternalActorRef getChild(Iterable<String> name) {
        return iterateRef(this, name.iterator());
    }


    private static final class ChildIterable<T> implements Iterable<T> {

        private Iterator<T> iterator;

        protected ChildIterable(Iterator<T> iterator) {
            this.iterator = iterator;
        }

        @Override
        public Iterator<T> iterator() {
            return iterator;
        }

    }

    private InternalActorRef iterateRef(InternalActorRef actorRef, Iterator<String> iterator) {
        if (iterator.hasNext()) {
            String nameNode = iterator.next();
            if (actorRef instanceof LocalActorRef) {
                switch (nameNode) {
                    case "..":
                        actorRef = actorRef.getParent();
                        break;
                    case "":
                        break;
                    default:
                        actorRef = ((LocalActorRef) actorRef).getSingleChild(nameNode);
                        break;
                }
                if (actorRef == ActorUtils.noBody()) {
                    return actorRef;
                } else {
                    return iterateRef(actorRef, iterator);
                }
            } else {
                return actorRef.getChild(new ChildIterable<>(iterator));
            }
        } else {
            return actorRef;
        }
    }

    private InternalActorRef getSingleChild(String name) {
        return actorCell.getSingleChild(name);
    }

    @Override
    public boolean isTerminated() {
        return actorCell.isTerminated();
    }

}

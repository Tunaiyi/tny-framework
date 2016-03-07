package drama.actor.local;

import drama.actor.ActorContext;
import drama.actor.ActorRef;
import drama.actor.PostboxFactory;

/**
 * Created by Kun Yang on 16/3/2.
 */
public class LocalActorContext<AR extends ActorRef<?>> extends ActorContext<AR> {

    private PostboxFactory postboxFactory;

//    private CopyOnWriteMap<String, AC>

    @Override
    public AR actorOf(String name) {
        return null;
    }

    @Override
    public AR actorOf() {
        return null;
    }

    @Override
    public void stop(AR actor) {

    }
}

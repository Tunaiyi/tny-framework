package com.tny.game.actor.local;


import com.tny.game.actor.Actor;
import com.tny.game.actor.ActorContext;
import com.tny.game.actor.ActorPath;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Kun Yang on 16/3/2.
 */
public class LocalActorContext<ID, M> implements ActorContext<ID, LocalTypeActor<ID, M>> {

    private ActorPath rootPath;

    private ActorTheatre defaultTheatre = ActorTheatres.getDefault();

    private ConcurrentHashMap<ID, LocalTypeActor<ID, M>> actorMap = new ConcurrentHashMap<>();

    private ActorProps defaultProps = ActorProps.of();

    public LocalActorContext(ActorPath rootPath) {
        this(rootPath, null, null);
    }

    public LocalActorContext(ActorPath rootPath, ActorTheatre defaultTheatre) {
        this(rootPath, null, defaultTheatre);
    }

    public LocalActorContext(ActorPath rootPath, ActorProps defaultProps) {
        this(rootPath, defaultProps, null);
    }

    public LocalActorContext(ActorPath rootPath, ActorProps defaultProps, ActorTheatre defaultTheatre) {
        this.rootPath = rootPath;
        if (defaultTheatre != null)
            this.defaultTheatre = defaultTheatre;
        if (defaultProps != null)
            this.defaultProps = defaultProps;
        if (this.defaultTheatre == null)
            this.defaultTheatre = ActorTheatres.getDefault();
        if (this.defaultProps == null)
            this.defaultProps = ActorProps.of();
    }

    public LocalTypeActor<ID, M> actorOf(ID id, ActorPath path, ActorProps props) {
        LocalTypeActor<ID, M> actor = actorMap.get(id);
        if (actor != null)
            return actor;
        ActorCell cell = new ActorCell(id, path, props == null ? defaultProps : props);
        actor = add(cell.getActor());
        if (!actor.isTakenOver()) {
            ActorTheatre theatre = props.getActorTheatre();
            if (theatre == null)
                theatre = defaultTheatre;
            theatre.takeOver(actor);
        }
        return add(actor);
    }

    public LocalTypeActor<ID, M> actorOf(ID id, ActorPath path, ActorTheatre theatre) {
        return actorOf(id, path, ActorProps.of(defaultProps).setActorTheatre(theatre));
    }

    @Override
    public LocalTypeActor<ID, M> actorOf(ID id, ActorPath path) {
        return actorOf(id, path, (ActorProps) null);
    }

    @Override
    public LocalTypeActor<ID, M> actorOf(ID id) {
        return actorOf(id, null, this.defaultProps);
    }

    protected boolean isExist(Actor<?, ?> actor) {
        return actorMap.get(actor.getActorID()) == actor;
    }

    protected boolean remove(Actor<?, ?> actor) {
        return actorMap.remove(actor.getActorID(), actor);
    }

    protected LocalTypeActor<ID, M> add(LocalTypeActor<ID, M> actor) {
        LocalTypeActor<ID, M> old = this.actorMap.putIfAbsent(actor.getActorID(), actor);
        return old != null ? old : actor;
    }

    @Override
    public boolean stop(Actor<?, ?> actor) {
        if (actor instanceof LocalTypeActor && this.isExist(actor)) {
            if (this.remove(actor)) {
                if (!actor.isTerminated())
                    ((LocalTypeActor) actor).terminate();
                return true;
            }
        }
        return false;
    }

    @Override
    public void stopAll() {
        Map<ID, LocalTypeActor<ID, M>> removes = this.actorMap;
        this.actorMap = new ConcurrentHashMap<>();
        removes.values().forEach(LocalTypeActor::terminate);
    }

}

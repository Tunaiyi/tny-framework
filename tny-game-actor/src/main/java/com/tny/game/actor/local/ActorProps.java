package com.tny.game.actor.local;

/**
 * Actor属性
 * Created by Kun Yang on 16/5/6.
 */
public class ActorProps {

    private ActorLifeCycle lifeCycle;

    private ActorHandler<?> actorHandler;

    private ActorTheatre actorTheatre;

    private ActorProps() {
    }

    public static ActorProps of() {
        return new ActorProps();
    }

    public static ActorProps of(ActorProps defaultProps) {
        return of()
                .setLifeCycle(defaultProps.getLifeCycle())
                .setActorHandler(defaultProps.getActorHandler())
                .setActorTheatre(defaultProps.getActorTheatre());
    }

    public ActorLifeCycle getLifeCycle() {
        return lifeCycle;
    }

    public ActorTheatre getActorTheatre() {
        return actorTheatre;
    }

    public ActorHandler<?> getActorHandler() {
        return actorHandler;
    }


    public ActorProps setActorHandler(ActorHandler<?> actorHandler) {
        this.actorHandler = actorHandler;
        return this;
    }

    public ActorProps setLifeCycle(ActorLifeCycle lifeCycle) {
        this.lifeCycle = lifeCycle;
        return this;
    }

    public ActorProps setActorTheatre(ActorTheatre actorTheatre) {
        this.actorTheatre = actorTheatre;
        return this;
    }

}

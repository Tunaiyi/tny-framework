package com.tny.game.actor.local;

/**
 * Actor属性
 * Created by Kun Yang on 16/5/6.
 */
public class ActorProps {

    private final static ActorCommandBoxFactory DEFAULT_BOX_FACTORY = new DefaultActorCommandBoxFactory();

    private static final ActorLifeCycle DEFAULT_LIFE_CYCLE = new ActorLifeCycle() {
    };

    private ActorLifeCycle lifeCycle;

    private ActorHandler<?> actorHandler;

    private ActorTheatre actorTheatre;

    private int stepSize = -1;

    private ActorCommandBoxFactory commandBoxFactory;

    private ActorProps() {
    }

    public static ActorProps of() {
        return new ActorProps();
    }

    public static ActorProps of(ActorProps defaultProps) {
        return of()
                .setLifeCycle(defaultProps.getLifeCycle())
                .setActorHandler(defaultProps.getActorHandler())
                .setActorTheatre(defaultProps.getActorTheatre())
                .setCommandBoxFactory(defaultProps.getCommandBoxFactory())
                .setStepSize(defaultProps.getStepSize());
    }

    public ActorLifeCycle getLifeCycle() {
        if (this.lifeCycle == null)
            this.lifeCycle = DEFAULT_LIFE_CYCLE;
        return lifeCycle;
    }

    public ActorTheatre getActorTheatre() {
        return actorTheatre;
    }

    public ActorHandler<?> getActorHandler() {
        return actorHandler;
    }

    public ActorCommandBoxFactory getCommandBoxFactory() {
        if (this.commandBoxFactory == null)
            this.commandBoxFactory = DEFAULT_BOX_FACTORY;
        return commandBoxFactory;
    }

    public ActorProps setActorHandler(ActorHandler<?> actorHandler) {
        this.actorHandler = actorHandler;
        return this;
    }

    public ActorProps setLifeCycle(ActorLifeCycle lifeCycle) {
        this.lifeCycle = new ProxyActorLifeCycle(lifeCycle);
        return this;
    }

    public ActorProps setActorTheatre(ActorTheatre actorTheatre) {
        this.actorTheatre = actorTheatre;
        return this;
    }

    public ActorProps setCommandBoxFactory(ActorCommandBoxFactory commandBoxFactory) {
        this.commandBoxFactory = commandBoxFactory;
        return this;
    }

    public ActorProps setStepSize(int stepSize) {
        this.stepSize = stepSize;
        return this;
    }

    public int getStepSize() {
        return stepSize;
    }
}

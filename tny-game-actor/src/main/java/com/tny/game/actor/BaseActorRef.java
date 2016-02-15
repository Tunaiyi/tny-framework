package com.tny.game.actor;

public abstract class BaseActorRef implements ActorRef {

    protected ActorPath path;

    protected BaseActorRef(ActorPath path) {
        this.path = path;
    }

    @Override
    public ActorPath getPath() {
        return path;
    }

    @Override
    public int hashCode() {
        return getPath().getAID();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof ActorRef))
            return false;
        ActorRef other = (ActorRef) obj;
        if (getPath() == null) {
            if (other.getPath() != null)
                return false;
        } else if (getPath().getAID() != other.getPath().getAID() || !path.equals(other.getPath()))
            return false;
        return true;
    }

}

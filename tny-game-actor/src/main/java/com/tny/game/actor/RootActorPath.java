package com.tny.game.actor;

public class RootActorPath extends ActorPath {

    @Override
    public int getAID() {
        return 0;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public ActorPath withUid(long aid) {
        return null;
    }

    @Override
    public ActorPath getParent() {
        return null;
    }

    @Override
    public ActorPath child(Iterable<String> child) {
        return null;
    }

    @Override
    public ActorPath child(String child) {
        return null;
    }

    @Override
    public Iterable<String> getElements() {
        return null;
    }

    @Override
    public RootActorPath root() {
        return null;
    }

    @Override
    public boolean isRoot() {
        return false;
    }

    @Override
    public String toStringWithoutAddress() {
        return null;
    }

    @Override
    public String toStringWithAddress(ActorAddress address) {
        return null;
    }

    @Override
    public String toSerializationFormat() {
        return null;
    }

    @Override
    public String toSerializationFormat(ActorAddress address) {
        return null;
    }

    @Override
    public ActorAddress getAddress() {
        return null;
    }
}
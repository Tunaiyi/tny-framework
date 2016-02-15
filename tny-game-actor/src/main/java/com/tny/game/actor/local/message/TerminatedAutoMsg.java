package com.tny.game.actor.local.message;

import com.tny.game.actor.ActorRef;

public class TerminatedAutoMsg extends BaseAutoReceivedMessage {

    private ActorRef actorRef;
    private boolean existenceConfirmed;
    private boolean addressTerminated;

    public static TerminatedAutoMsg message(ActorRef actorRef, boolean existenceConfirmed, boolean addressTerminated) {
        return new TerminatedAutoMsg(actorRef, existenceConfirmed, addressTerminated);
    }

    private TerminatedAutoMsg(ActorRef actorRef, boolean existenceConfirmed, boolean addressTerminated) {
        super(AutoReceivedMessageType.TERMINATED);
        this.actorRef = actorRef;
        this.existenceConfirmed = existenceConfirmed;
        this.addressTerminated = addressTerminated;
    }

    public ActorRef getActorRef() {
        return actorRef;
    }

    public boolean isExistenceConfirmed() {
        return existenceConfirmed;
    }

    public boolean isAddressTerminated() {
        return addressTerminated;
    }
}

package com.tny.game.actor.local;

import com.tny.game.actor.Actor;
import com.tny.game.actor.ActorPath;
import com.tny.game.actor.Answer;
import com.tny.game.actor.invoke.*;
import com.tny.game.actor.local.LocalAsker.LocalA0Asker;
import com.tny.game.actor.local.LocalAsker.LocalA1Asker;
import com.tny.game.actor.local.LocalAsker.LocalA2Asker;
import com.tny.game.actor.local.LocalAsker.LocalA3Asker;
import com.tny.game.actor.local.LocalAsker.LocalA4Asker;
import com.tny.game.actor.local.LocalAsker.LocalA5Asker;
import com.tny.game.actor.local.LocalAsker.LocalA6Asker;
import com.tny.game.actor.local.LocalAsker.LocalA7Asker;
import com.tny.game.actor.local.LocalAsker.LocalUntilAsker;
import com.tny.game.actor.local.LocalTeller.LocalA0Teller;
import com.tny.game.actor.local.LocalTeller.LocalA1Teller;
import com.tny.game.actor.local.LocalTeller.LocalA2Teller;
import com.tny.game.actor.local.LocalTeller.LocalA3Teller;
import com.tny.game.actor.local.LocalTeller.LocalA4Teller;
import com.tny.game.actor.local.LocalTeller.LocalA5Teller;
import com.tny.game.actor.local.LocalTeller.LocalA6Teller;
import com.tny.game.actor.local.LocalTeller.LocalA7Teller;
import com.tny.game.actor.local.LocalTeller.LocalUntilTeller;
import com.tny.game.actor.stage.TypeTaskStage;
import com.tny.game.common.utils.Done;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;


/**
 * 本地Actor对象
 * Created by Kun Yang on 16/4/25.
 */
class LocalTypeActor<ID, M> extends LocalActor<ID, M> {

    private ID actorID;

    private ActorCell actorCell;

    LocalTypeActor(ID actorID, ActorCell cell) {
        this.actorID = actorID;
        this.actorCell = cell;
    }

    @Override
    protected boolean detach() {
        return this.actorCell.detach();
    }

    @Override
    public String getName() {
        return this.actorID.toString();
    }

    @Override
    public boolean takeOver(LocalActor<?, ?> actor) {
        return actorCell.getCommandBox().register((actor).cell().getCommandBox());
    }


    @Override
    public ActorCell cell() {
        return actorCell;
    }

    @Override
    protected void terminate() {
        actorCell.terminate();
    }

    @Override
    public ID getActorID() {
        return actorID;
    }

    @Override
    public ActorPath getPath() {
        return null;
    }

    @Override
    public boolean isTerminated() {
        return actorCell.isTerminated();
    }

    @Override
    public boolean isTakenOver() {
        return actorCell.isTakenOver();
    }

    @Override
    public boolean isLocal() {
        return true;
    }

    @Override
    public void tell(M message) {
        actorCell.tell(message, null);
    }


    @Override
    public void tell(M message, Actor sender) {
        actorCell.tell(message, sender);
    }


    @Override
    public <V> Answer<V> ask(M message) {
        return actorCell.ask(message, null);
    }

    @Override
    public <V> Answer<V> ask(M message, Actor sender) {
        return actorCell.ask(message, sender);
    }


    @Override
    public A0Teller tellUntil(Supplier<Boolean> supplier) {
        return new LocalUntilTeller(actorCell, supplier);
    }

    @Override
    public A0Teller tellUntil(Predicate<LocalActor> predicate) {
        return new LocalUntilTeller(actorCell, predicate);
    }

    @Override
    public <T> A0Asker<T, TypeTaskStage<T>> askUntil(Supplier<Done<T>> supplier) {
        return new LocalUntilAsker<>(actorCell, supplier);
    }

    @Override
    public <T> A0Asker<T, TypeTaskStage<T>> askUntil(Function<LocalActor, Done<T>> function) {
        return new LocalUntilAsker<>(actorCell, function);
    }

    @Override
    public A0Teller tellToAccept(A0Acceptor<Actor<ID, M>> acceptor) {
        return new LocalA0Teller(actorCell, acceptor);
    }

    @Override
    public <A1> A1Teller<A1> tellToAccept(A1Acceptor<Actor<ID, M>, A1> acceptor) {
        return new LocalA1Teller<>(actorCell, acceptor);
    }

    @Override
    public <A1, A2> A2Teller<A1, A2> tellToAccept(A2Acceptor<Actor<ID, M>, A1, A2> acceptor) {
        return new LocalA2Teller<>(actorCell, acceptor);
    }

    @Override
    public <A1, A2, A3> A3Teller<A1, A2, A3> tellToAccept(A3Acceptor<Actor<ID, M>, A1, A2, A3> acceptor) {
        return new LocalA3Teller<>(actorCell, acceptor);
    }

    @Override
    public <A1, A2, A3, A4> A4Teller<A1, A2, A3, A4> tellToAccept(A4Acceptor<Actor<ID, M>, A1, A2, A3, A4> acceptor) {
        return new LocalA4Teller<>(actorCell, acceptor);
    }

    @Override
    public <A1, A2, A3, A4, A5> A5Teller<A1, A2, A3, A4, A5> tellToAccept(A5Acceptor<Actor<ID, M>, A1, A2, A3, A4, A5> acceptor) {
        return new LocalA5Teller<>(actorCell, acceptor);
    }

    @Override
    public <A1, A2, A3, A4, A5, A6> A6Teller<A1, A2, A3, A4, A5, A6> tellToAccept(A6Acceptor<Actor<ID, M>, A1, A2, A3, A4, A5, A6> acceptor) {
        return new LocalA6Teller<>(actorCell, acceptor);
    }

    @Override
    public <A1, A2, A3, A4, A5, A6, A7> A7Teller<A1, A2, A3, A4, A5, A6, A7> tellToAccept(A7Acceptor<Actor<ID, M>, A1, A2, A3, A4, A5, A6, A7> acceptor) {
        return new LocalA7Teller<>(actorCell, acceptor);
    }

    @Override
    public A0Teller tellToRun(A0Runner runner) {
        return new LocalA0Teller(actorCell, runner);
    }

    @Override
    public <A1> A1Teller<A1> tellToRun(A1Runner<A1> runner) {
        return new LocalA1Teller<>(actorCell, runner);
    }

    @Override
    public <A1, A2> A2Teller<A1, A2> tellToRun(A2Runner<A1, A2> runner) {
        return new LocalA2Teller<>(actorCell, runner);
    }

    @Override
    public <A1, A2, A3> A3Teller<A1, A2, A3> tellToRun(A3Runner<A1, A2, A3> runner) {
        return new LocalA3Teller<>(actorCell, runner);
    }

    @Override
    public <A1, A2, A3, A4> A4Teller<A1, A2, A3, A4> tellToRun(A4Runner<A1, A2, A3, A4> runner) {
        return new LocalA4Teller<>(actorCell, runner);
    }

    @Override
    public <A1, A2, A3, A4, A5> A5Teller<A1, A2, A3, A4, A5> tellToRun(A5Runner<A1, A2, A3, A4, A5> runner) {
        return new LocalA5Teller<>(actorCell, runner);
    }

    @Override
    public <A1, A2, A3, A4, A5, A6> A6Teller<A1, A2, A3, A4, A5, A6> tellToRun(A6Runner<A1, A2, A3, A4, A5, A6> runner) {
        return new LocalA6Teller<>(actorCell, runner);
    }

    @Override
    public <A1, A2, A3, A4, A5, A6, A7> A7Teller<A1, A2, A3, A4, A5, A6, A7> tellToRun(A7Runner<A1, A2, A3, A4, A5, A6, A7> runner) {
        return new LocalA7Teller<>(actorCell, runner);
    }

    @Override
    public <R> A0Asker<R, TypeTaskStage<R>> askToAnswer(A0Answerer<Actor<ID, M>, R> answerer) {
        return new LocalA0Asker<>(actorCell, answerer);
    }

    @Override
    public <R, A1> A1Asker<R, TypeTaskStage<R>, A1> askToAnswer(A1Answerer<Actor<ID, M>, R, A1> answerer) {
        return new LocalA1Asker<>(actorCell, answerer);
    }

    @Override
    public <R, A1, A2> A2Asker<R, TypeTaskStage<R>, A1, A2> askToAnswer(A2Answerer<Actor<ID, M>, R, A1, A2> answerer) {
        return new LocalA2Asker<>(actorCell, answerer);
    }

    @Override
    public <R, A1, A2, A3> A3Asker<R, TypeTaskStage<R>, A1, A2, A3> askToAnswer(A3Answerer<Actor<ID, M>, R, A1, A2, A3> answerer) {
        return new LocalA3Asker<>(actorCell, answerer);
    }

    @Override
    public <R, A1, A2, A3, A4> A4Asker<R, TypeTaskStage<R>, A1, A2, A3, A4> askToAnswer(A4Answerer<Actor<ID, M>, R, A1, A2, A3, A4> answerer) {
        return new LocalA4Asker<>(actorCell, answerer);
    }

    @Override
    public <R, A1, A2, A3, A4, A5> A5Asker<R, TypeTaskStage<R>, A1, A2, A3, A4, A5> askToAnswer(A5Answerer<Actor<ID, M>, R, A1, A2, A3, A4, A5> answerer) {
        return new LocalA5Asker<>(actorCell, answerer);
    }

    @Override
    public <R, A1, A2, A3, A4, A5, A6> A6Asker<R, TypeTaskStage<R>, A1, A2, A3, A4, A5, A6> askToAnswer(A6Answerer<Actor<ID, M>, R, A1, A2, A3, A4, A5, A6> answerer) {
        return new LocalA6Asker<>(actorCell, answerer);
    }

    @Override
    public <R, A1, A2, A3, A4, A5, A6, A7> A7Asker<R, TypeTaskStage<R>, A1, A2, A3, A4, A5, A6, A7> askToAnswer(A7Answerer<Actor<ID, M>, R, A1, A2, A3, A4, A5, A6, A7> answerer) {
        return new LocalA7Asker<>(actorCell, answerer);
    }

    @Override
    public <R> A0Asker<R, TypeTaskStage<R>> askToCall(A0Caller<R> caller) {
        return new LocalA0Asker<>(actorCell, caller);
    }

    @Override
    public <R, A1> A1Asker<R, TypeTaskStage<R>, A1> askToCall(A1Caller<R, A1> caller) {
        return new LocalA1Asker<>(actorCell, caller);
    }

    @Override
    public <R, A1, A2> A2Asker<R, TypeTaskStage<R>, A1, A2> askToCall(A2Caller<R, A1, A2> caller) {
        return new LocalA2Asker<>(actorCell, caller);
    }

    @Override
    public <R, A1, A2, A3> A3Asker<R, TypeTaskStage<R>, A1, A2, A3> askToCall(A3Caller<R, A1, A2, A3> caller) {
        return new LocalA3Asker<>(actorCell, caller);
    }

    @Override
    public <R, A1, A2, A3, A4> A4Asker<R, TypeTaskStage<R>, A1, A2, A3, A4> askToCall(A4Caller<R, A1, A2, A3, A4> caller) {
        return new LocalA4Asker<>(actorCell, caller);
    }

    @Override
    public <R, A1, A2, A3, A4, A5> A5Asker<R, TypeTaskStage<R>, A1, A2, A3, A4, A5> askToCall(A5Caller<R, A1, A2, A3, A4, A5> caller) {
        return new LocalA5Asker<>(actorCell, caller);
    }

    @Override
    public <R, A1, A2, A3, A4, A5, A6> A6Asker<R, TypeTaskStage<R>, A1, A2, A3, A4, A5, A6> askToCall(A6Caller<R, A1, A2, A3, A4, A5, A6> caller) {
        return new LocalA6Asker<>(actorCell, caller);
    }

    @Override
    public <R, A1, A2, A3, A4, A5, A6, A7> A7Asker<R, TypeTaskStage<R>, A1, A2, A3, A4, A5, A6, A7> askToCall(A7Caller<R, A1, A2, A3, A4, A5, A6, A7> caller) {
        return new LocalA7Asker<>(actorCell, caller);
    }

}

/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.actor.local;

import com.tny.game.actor.*;

/**
 * 本地Actor对象
 * Created by Kun Yang on 16/4/25.
 */
class DefaultLocalActor<ID, M> extends LocalActor<ID, M> {

    private ID actorID;

    private ActorCell actorCell;

    DefaultLocalActor(ID actorID, ActorCell cell) {
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
    public ID getActorId() {
        return actorID;
    }

    @Override
    public ActorURL getURL() {
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
        return actorCell.ask(message, this);
    }

    @Override
    public <V> Answer<V> ask(M message, Actor sender) {
        return actorCell.ask(message, sender);
    }

    @Override
    public void execute(Runnable command) {
        actorCell.sendMessage(new ActorRunCommand(this.actorCell, command), this, false);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LocalTypeActor{");
        sb.append("actorID=").append(actorID);
        sb.append(", actorCell=").append(actorCell);
        sb.append('}');
        return sb.toString();
    }
    // @Override
    // public A0Teller asWaitTeller(Supplier<Boolean> supplier) {
    //     return new LocalUntilTeller(actorCell, supplier);
    // }
    //
    // @Override
    // public A0Teller asWaitTeller(Predicate<LocalActor> predicate) {
    //     return new LocalUntilTeller(actorCell, predicate);
    // }
    //
    // @Override
    // public <T> A0Asker<T, Stage<T>> asWaitAsker(Supplier<Done<T>> supplier) {
    //     return new LocalUntilAsker<>(actorCell, supplier);
    // }
    //
    // @Override
    // public <T> A0Asker<T, Stage<T>> asWaitAsker(Function<LocalActor, Done<T>> function) {
    //     return new LocalUntilAsker<>(actorCell, function);
    // }
    //
    // @Override
    // public A0Teller asTeller(A0Acceptor<Actor<ID, M>> acceptor) {
    //     return new LocalA0Teller(actorCell, acceptor);
    // }
    //
    // @Override
    // public <A1> A1Teller<A1> asTeller(A1Acceptor<Actor<ID, M>, A1> acceptor) {
    //     return new LocalA1Teller<>(actorCell, acceptor);
    // }
    //
    // @Override
    // public <A1, A2> A2Teller<A1, A2> asTeller(A2Acceptor<Actor<ID, M>, A1, A2> acceptor) {
    //     return new LocalA2Teller<>(actorCell, acceptor);
    // }
    //
    // @Override
    // public <A1, A2, A3> A3Teller<A1, A2, A3> asTeller(A3Acceptor<Actor<ID, M>, A1, A2, A3> acceptor) {
    //     return new LocalA3Teller<>(actorCell, acceptor);
    // }
    //
    // @Override
    // public <A1, A2, A3, A4> A4Teller<A1, A2, A3, A4> asTeller(A4Acceptor<Actor<ID, M>, A1, A2, A3, A4> acceptor) {
    //     return new LocalA4Teller<>(actorCell, acceptor);
    // }
    //
    // @Override
    // public <A1, A2, A3, A4, A5> A5Teller<A1, A2, A3, A4, A5> asTeller(A5Acceptor<Actor<ID, M>, A1, A2, A3, A4, A5> acceptor) {
    //     return new LocalA5Teller<>(actorCell, acceptor);
    // }
    //
    // @Override
    // public <A1, A2, A3, A4, A5, A6> A6Teller<A1, A2, A3, A4, A5, A6> asTeller(A6Acceptor<Actor<ID, M>, A1, A2, A3, A4, A5, A6> acceptor) {
    //     return new LocalA6Teller<>(actorCell, acceptor);
    // }
    //
    // @Override
    // public <A1, A2, A3, A4, A5, A6, A7> A7Teller<A1, A2, A3, A4, A5, A6, A7> asTeller(A7Acceptor<Actor<ID, M>, A1, A2, A3, A4, A5, A6, A7>
    // acceptor) {
    //     return new LocalA7Teller<>(actorCell, acceptor);
    // }
    //
    // @Override
    // public A0Teller asTeller(A0Runner runner) {
    //     return new LocalA0Teller(actorCell, runner);
    // }
    //
    // @Override
    // public <A1> A1Teller<A1> asTeller(A1Runner<A1> runner) {
    //     return new LocalA1Teller<>(actorCell, runner);
    // }
    //
    // @Override
    // public <A1, A2> A2Teller<A1, A2> asTeller(A2Runner<A1, A2> runner) {
    //     return new LocalA2Teller<>(actorCell, runner);
    // }
    //
    // @Override
    // public <A1, A2, A3> A3Teller<A1, A2, A3> asTeller(A3Runner<A1, A2, A3> runner) {
    //     return new LocalA3Teller<>(actorCell, runner);
    // }
    //
    // @Override
    // public <A1, A2, A3, A4> A4Teller<A1, A2, A3, A4> asTeller(A4Runner<A1, A2, A3, A4> runner) {
    //     return new LocalA4Teller<>(actorCell, runner);
    // }
    //
    // @Override
    // public <A1, A2, A3, A4, A5> A5Teller<A1, A2, A3, A4, A5> asTeller(A5Runner<A1, A2, A3, A4, A5> runner) {
    //     return new LocalA5Teller<>(actorCell, runner);
    // }
    //
    // @Override
    // public <A1, A2, A3, A4, A5, A6> A6Teller<A1, A2, A3, A4, A5, A6> asTeller(A6Runner<A1, A2, A3, A4, A5, A6> runner) {
    //     return new LocalA6Teller<>(actorCell, runner);
    // }
    //
    // @Override
    // public <A1, A2, A3, A4, A5, A6, A7> A7Teller<A1, A2, A3, A4, A5, A6, A7> asTeller(A7Runner<A1, A2, A3, A4, A5, A6, A7> runner) {
    //     return new LocalA7Teller<>(actorCell, runner);
    // }
    //
    // @Override
    // public <R> A0Asker<R, Stage<R>> asAsker(A0Answerer<Actor<ID, M>, R> answerer) {
    //     return new LocalA0Asker<>(actorCell, answerer);
    // }
    //
    // @Override
    // public <R, A1> A1Asker<R, Stage<R>, A1> asAsker(A1Answerer<Actor<ID, M>, R, A1> answerer) {
    //     return new LocalA1Asker<>(actorCell, answerer);
    // }
    //
    // @Override
    // public <R, A1, A2> A2Asker<R, Stage<R>, A1, A2> asAsker(A2Answerer<Actor<ID, M>, R, A1, A2> answerer) {
    //     return new LocalA2Asker<>(actorCell, answerer);
    // }
    //
    // @Override
    // public <R, A1, A2, A3> A3Asker<R, Stage<R>, A1, A2, A3> asAsker(A3Answerer<Actor<ID, M>, R, A1, A2, A3> answerer) {
    //     return new LocalA3Asker<>(actorCell, answerer);
    // }
    //
    // @Override
    // public <R, A1, A2, A3, A4> A4Asker<R, Stage<R>, A1, A2, A3, A4> asAsker(A4Answerer<Actor<ID, M>, R, A1, A2, A3, A4> answerer) {
    //     return new LocalA4Asker<>(actorCell, answerer);
    // }
    //
    // @Override
    // public <R, A1, A2, A3, A4, A5> A5Asker<R, Stage<R>, A1, A2, A3, A4, A5> asAsker(A5Answerer<Actor<ID, M>, R, A1, A2, A3, A4, A5> answerer) {
    //     return new LocalA5Asker<>(actorCell, answerer);
    // }
    //
    // @Override
    // public <R, A1, A2, A3, A4, A5, A6> A6Asker<R, Stage<R>, A1, A2, A3, A4, A5, A6> asAsker(A6Answerer<Actor<ID, M>, R, A1, A2, A3, A4, A5, A6>
    // answerer) {
    //     return new LocalA6Asker<>(actorCell, answerer);
    // }
    //
    // @Override
    // public <R, A1, A2, A3, A4, A5, A6, A7> A7Asker<R, Stage<R>, A1, A2, A3, A4, A5, A6, A7> asAsker(A7Answerer<Actor<ID, M>, R, A1, A2, A3, A4,
    // A5, A6, A7> answerer) {
    //     return new LocalA7Asker<>(actorCell, answerer);
    // }
    //
    // @Override
    // public <R> A0Asker<R, Stage<R>> asAsker(A0Caller<R> caller) {
    //     return new LocalA0Asker<>(actorCell, caller);
    // }
    //
    // @Override
    // public <R, A1> A1Asker<R, Stage<R>, A1> asAsker(A1Caller<R, A1> caller) {
    //     return new LocalA1Asker<>(actorCell, caller);
    // }
    //
    // @Override
    // public <R, A1, A2> A2Asker<R, Stage<R>, A1, A2> asAsker(A2Caller<R, A1, A2> caller) {
    //     return new LocalA2Asker<>(actorCell, caller);
    // }
    //
    // @Override
    // public <R, A1, A2, A3> A3Asker<R, Stage<R>, A1, A2, A3> asAsker(A3Caller<R, A1, A2, A3> caller) {
    //     return new LocalA3Asker<>(actorCell, caller);
    // }
    //
    // @Override
    // public <R, A1, A2, A3, A4> A4Asker<R, Stage<R>, A1, A2, A3, A4> asAsker(A4Caller<R, A1, A2, A3, A4> caller) {
    //     return new LocalA4Asker<>(actorCell, caller);
    // }
    //
    // @Override
    // public <R, A1, A2, A3, A4, A5> A5Asker<R, Stage<R>, A1, A2, A3, A4, A5> asAsker(A5Caller<R, A1, A2, A3, A4, A5> caller) {
    //     return new LocalA5Asker<>(actorCell, caller);
    // }
    //
    // @Override
    // public <R, A1, A2, A3, A4, A5, A6> A6Asker<R, Stage<R>, A1, A2, A3, A4, A5, A6> asAsker(A6Caller<R, A1, A2, A3, A4, A5, A6> caller) {
    //     return new LocalA6Asker<>(actorCell, caller);
    // }
    //
    // @Override
    // public <R, A1, A2, A3, A4, A5, A6, A7> A7Asker<R, Stage<R>, A1, A2, A3, A4, A5, A6, A7> asAsker(A7Caller<R, A1, A2, A3, A4, A5, A6, A7>
    // caller) {
    //     return new LocalA7Asker<>(actorCell, caller);
    // }

}

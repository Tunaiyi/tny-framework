package com.tny.game.actor;


import com.tny.game.actor.invoke.*;
import com.tny.game.actor.local.LocalActor;
import com.tny.game.actor.stage.TypeTaskStage;
import com.tny.game.common.utils.Done;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author KGTny
 */
public interface ActorInvoker<ID, ACT extends Actor<ID, ?>> {

    A0Teller tellUntil(Supplier<Boolean> supplier);

    A0Teller tellUntil(Predicate<LocalActor> predicate);

    <T> A0Asker<T, TypeTaskStage<T>> askUntil(Supplier<Done<T>> supplier);

    <T> A0Asker<T, TypeTaskStage<T>> askUntil(Function<LocalActor, Done<T>> predicate);

    A0Teller tellToAccept(A0Acceptor<ACT> acceptor);

    <A1> A1Teller<A1> tellToAccept(A1Acceptor<ACT, A1> acceptor);

    <A1, A2> A2Teller<A1, A2> tellToAccept(A2Acceptor<ACT, A1, A2> acceptor);

    <A1, A2, A3> A3Teller<A1, A2, A3> tellToAccept(A3Acceptor<ACT, A1, A2, A3> acceptor);

    <A1, A2, A3, A4> A4Teller<A1, A2, A3, A4> tellToAccept(A4Acceptor<ACT, A1, A2, A3, A4> acceptor);

    <A1, A2, A3, A4, A5> A5Teller<A1, A2, A3, A4, A5> tellToAccept(A5Acceptor<ACT, A1, A2, A3, A4, A5> acceptor);

    <A1, A2, A3, A4, A5, A6> A6Teller<A1, A2, A3, A4, A5, A6> tellToAccept(A6Acceptor<ACT, A1, A2, A3, A4, A5, A6> acceptor);

    <A1, A2, A3, A4, A5, A6, A7> A7Teller<A1, A2, A3, A4, A5, A6, A7> tellToAccept(A7Acceptor<ACT, A1, A2, A3, A4, A5, A6, A7> acceptor);

    A0Teller tellToRun(A0Runner runner);

    <A1> A1Teller<A1> tellToRun(A1Runner<A1> runner);

    <A1, A2> A2Teller<A1, A2> tellToRun(A2Runner<A1, A2> runner);

    <A1, A2, A3> A3Teller<A1, A2, A3> tellToRun(A3Runner<A1, A2, A3> runner);

    <A1, A2, A3, A4> A4Teller<A1, A2, A3, A4> tellToRun(A4Runner<A1, A2, A3, A4> runner);

    <A1, A2, A3, A4, A5> A5Teller<A1, A2, A3, A4, A5> tellToRun(A5Runner<A1, A2, A3, A4, A5> runner);

    <A1, A2, A3, A4, A5, A6> A6Teller<A1, A2, A3, A4, A5, A6> tellToRun(A6Runner<A1, A2, A3, A4, A5, A6> runner);

    <A1, A2, A3, A4, A5, A6, A7> A7Teller<A1, A2, A3, A4, A5, A6, A7> tellToRun(A7Runner<A1, A2, A3, A4, A5, A6, A7> runner);

    <R> A0Asker<R, TypeTaskStage<R>> askToAnswer(A0Answerer<ACT, R> answerer);

    <R, A1> A1Asker<R, TypeTaskStage<R>, A1> askToAnswer(A1Answerer<ACT, R, A1> answerer);

    <R, A1, A2> A2Asker<R, TypeTaskStage<R>, A1, A2> askToAnswer(A2Answerer<ACT, R, A1, A2> answerer);

    <R, A1, A2, A3> A3Asker<R, TypeTaskStage<R>, A1, A2, A3> askToAnswer(A3Answerer<ACT, R, A1, A2, A3> answerer);

    <R, A1, A2, A3, A4> A4Asker<R, TypeTaskStage<R>, A1, A2, A3, A4> askToAnswer(A4Answerer<ACT, R, A1, A2, A3, A4> answerer);

    <R, A1, A2, A3, A4, A5> A5Asker<R, TypeTaskStage<R>, A1, A2, A3, A4, A5> askToAnswer(A5Answerer<ACT, R, A1, A2, A3, A4, A5> answerer);

    <R, A1, A2, A3, A4, A5, A6> A6Asker<R, TypeTaskStage<R>, A1, A2, A3, A4, A5, A6> askToAnswer(A6Answerer<ACT, R, A1, A2, A3, A4, A5, A6> answerer);

    <R, A1, A2, A3, A4, A5, A6, A7> A7Asker<R, TypeTaskStage<R>, A1, A2, A3, A4, A5, A6, A7> askToAnswer(A7Answerer<ACT, R, A1, A2, A3, A4, A5, A6, A7> answerer);

    <R> A0Asker<R, TypeTaskStage<R>> askToCall(A0Caller<R> caller);

    <R, A1> A1Asker<R, TypeTaskStage<R>, A1> askToCall(A1Caller<R, A1> caller);

    <R, A1, A2> A2Asker<R, TypeTaskStage<R>, A1, A2> askToCall(A2Caller<R, A1, A2> caller);

    <R, A1, A2, A3> A3Asker<R, TypeTaskStage<R>, A1, A2, A3> askToCall(A3Caller<R, A1, A2, A3> caller);

    <R, A1, A2, A3, A4> A4Asker<R, TypeTaskStage<R>, A1, A2, A3, A4> askToCall(A4Caller<R, A1, A2, A3, A4> caller);

    <R, A1, A2, A3, A4, A5> A5Asker<R, TypeTaskStage<R>, A1, A2, A3, A4, A5> askToCall(A5Caller<R, A1, A2, A3, A4, A5> caller);

    <R, A1, A2, A3, A4, A5, A6> A6Asker<R, TypeTaskStage<R>, A1, A2, A3, A4, A5, A6> askToCall(A6Caller<R, A1, A2, A3, A4, A5, A6> caller);

    <R, A1, A2, A3, A4, A5, A6, A7> A7Asker<R, TypeTaskStage<R>, A1, A2, A3, A4, A5, A6, A7> askToCall(A7Caller<R, A1, A2, A3, A4, A5, A6, A7> caller);

}
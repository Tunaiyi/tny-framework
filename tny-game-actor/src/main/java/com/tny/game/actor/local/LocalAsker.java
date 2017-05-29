package com.tny.game.actor.local;


import com.tny.game.actor.Actor;
import com.tny.game.actor.TypeAnswer;
import com.tny.game.actor.invoke.*;
import com.tny.game.actor.stage.Stage;
import com.tny.game.actor.stage.Stages;
import com.tny.game.actor.stage.TypeStage;
import com.tny.game.common.utils.Done;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
class LocalAsker<R, AK extends Asker, C> implements Asker<AK, TypeStage<R>> {

    protected ActorCommand<R, TypeStage<R>, LocalTypeAnswer<R>> command;

    protected TypeStage<R> stage;

    protected ActorCell actorCell;

    protected C caller;

    public Done<R> getDone() {
        return command.getHandleResult();
    }

    @Override
    @SuppressWarnings("unchecked")
    public AK then(Consumer<TypeStage<R>> initStage) {
        this.stage = Stages.waitFor(this::getDone);
        initStage.accept(this.stage);
        return (AK) this;
    }

    protected TypeStage<R> stage() {
        if (this.stage == null)
            this.stage = Stages.waitFor(this::getDone);
        return this.stage;
    }


    static class LocalUntilAsker<R> extends LocalAsker<R, A0Asker<R, TypeStage<R>>, Supplier<Done<R>>> implements A0Asker<R, TypeStage<R>> {


        LocalUntilAsker(ActorCell actorCell, Function<LocalActor, Done<R>> function) {
            this.actorCell = actorCell;
            this.caller = () -> function.apply(this.actorCell.getActor());
        }

        public LocalUntilAsker(ActorCell actorCell, Supplier<Done<R>> supplier) {
            this.actorCell = actorCell;
            this.caller = supplier;
        }

        @Override
        public TypeAnswer<R> ask() {
            return this.actorCell.ask(command = new ActorCallUntilCommand<>(actorCell, (actor) -> caller.get(), new LocalTypeAnswer<>(), this.stage), null);
        }

        @Override
        public Stage asking() {
            this.ask();
            return this.stage();
        }

    }

    static class LocalA0Asker<R> extends LocalAsker<R, A0Asker<R, TypeStage<R>>, A0Caller<R>> implements A0Asker<R, TypeStage<R>> {

        LocalA0Asker(ActorCell actorCell, A0Caller caller) {
            this.actorCell = actorCell;
            this.caller = caller;
        }

        LocalA0Asker(ActorCell actorCell, A0Answerer<? extends Actor, R> answerer) {
            this.actorCell = actorCell;
            this.caller = () -> answerer.answer(this.actorCell.getActor());
        }

        @Override
        public TypeAnswer<R> ask() {
            return this.actorCell.ask(command = new ActorCallCommand<>(actorCell, () -> caller.call(), new LocalTypeAnswer<>(), this.stage), null);
        }

        @Override
        public Stage asking() {
            this.ask();
            return this.stage();
        }

    }

    static class LocalA1Asker<R, A1> extends LocalAsker<R, A1Asker<R, TypeStage<R>, A1>, A1Caller<R, A1>> implements A1Asker<R, TypeStage<R>, A1> {

        LocalA1Asker(ActorCell actorCell, A1Caller caller) {
            this.actorCell = actorCell;
            this.caller = caller;
        }

        LocalA1Asker(ActorCell actorCell, A1Answerer<? extends Actor, R, A1> answerer) {
            this.actorCell = actorCell;
            this.caller = (a1) -> answerer.answer(this.actorCell.getActor(), a1);
        }

        @Override
        public TypeAnswer<R> ask(A1 a1) {
            return this.actorCell.ask(command = new ActorCallCommand(actorCell, () -> caller.call(a1), new LocalTypeAnswer<>(), this.stage), null);
        }

        @Override
        public Stage asking(A1 arg1) {
            this.ask(arg1);
            return this.stage();
        }
    }

    static class LocalA2Asker<R, A1, A2> extends LocalAsker<R, A2Asker<R, TypeStage<R>, A1, A2>, A2Caller<R, A1, A2>> implements A2Asker<R, TypeStage<R>, A1, A2> {

        LocalA2Asker(ActorCell actorCell, A2Caller caller) {
            this.actorCell = actorCell;
            this.caller = caller;
        }

        LocalA2Asker(ActorCell actorCell, A2Answerer<? extends Actor, R, A1, A2> answerer) {
            this.actorCell = actorCell;
            this.caller = (a1, a2) -> answerer.answer(this.actorCell.getActor(), a1, a2);
        }

        @Override
        public TypeAnswer<R> ask(A1 a1, A2 a2) {
            return this.actorCell.ask(command = new ActorCallCommand(actorCell, () -> caller.call(a1, a2), new LocalTypeAnswer<>(), this.stage), null);
        }

        @Override
        public Stage asking(A1 arg1, A2 arg2) {
            this.ask(arg1, arg2);
            return this.stage();
        }
    }

    static class LocalA3Asker<R, A1, A2, A3> extends LocalAsker<R, A3Asker<R, TypeStage<R>, A1, A2, A3>, A3Caller<R, A1, A2, A3>> implements A3Asker<R, TypeStage<R>, A1, A2, A3> {

        LocalA3Asker(ActorCell actorCell, A3Caller caller) {
            this.actorCell = actorCell;
            this.caller = caller;
        }

        LocalA3Asker(ActorCell actorCell, A3Answerer<? extends Actor, R, A1, A2, A3> answerer) {
            this.actorCell = actorCell;
            this.caller = (a1, a2, a3) -> answerer.answer(this.actorCell.getActor(), a1, a2, a3);
        }

        @Override
        public TypeAnswer<R> ask(A1 a1, A2 a2, A3 a3) {
            return this.actorCell.ask(command = new ActorCallCommand(actorCell, () -> caller.call(a1, a2, a3), new LocalTypeAnswer<>(), this.stage), null);
        }

        @Override
        public Stage asking(A1 arg1, A2 arg2, A3 arg3) {
            this.ask(arg1, arg2, arg3);
            return this.stage();
        }
    }


    static class LocalA4Asker<R, A1, A2, A3, A4> extends LocalAsker<R, A4Asker<R, TypeStage<R>, A1, A2, A3, A4>, A4Caller<R, A1, A2, A3, A4>> implements A4Asker<R, TypeStage<R>, A1, A2, A3, A4> {

        LocalA4Asker(ActorCell actorCell, A4Caller caller) {
            this.actorCell = actorCell;
            this.caller = caller;
        }

        LocalA4Asker(ActorCell actorCell, A4Answerer<? extends Actor, R, A1, A2, A3, A4> answerer) {
            this.actorCell = actorCell;
            this.caller = (a1, a2, a3, a4) -> answerer.answer(this.actorCell.getActor(), a1, a2, a3, a4);
        }

        @Override
        public TypeAnswer<R> ask(A1 a1, A2 a2, A3 a3, A4 a4) {
            return this.actorCell.ask(command = new ActorCallCommand(actorCell, () -> caller.call(a1, a2, a3, a4), new LocalTypeAnswer<>(), this.stage), null);
        }

        @Override
        public Stage asking(A1 arg1, A2 arg2, A3 arg3, A4 arg4) {
            this.ask(arg1, arg2, arg3, arg4);
            return this.stage();
        }
    }


    static class LocalA5Asker<R, A1, A2, A3, A4, A5> extends LocalAsker<R, A5Asker<R, TypeStage<R>, A1, A2, A3, A4, A5>, A5Caller<R, A1, A2, A3, A4, A5>> implements A5Asker<R, TypeStage<R>, A1, A2, A3, A4, A5> {

        LocalA5Asker(ActorCell actorCell, A5Caller caller) {
            this.actorCell = actorCell;
            this.caller = caller;
        }

        LocalA5Asker(ActorCell actorCell, A5Answerer<? extends Actor, R, A1, A2, A3, A4, A5> answerer) {
            this.actorCell = actorCell;
            this.caller = (a1, a2, a3, a4, a5) -> answerer.answer(this.actorCell.getActor(), a1, a2, a3, a4, a5);
        }

        @Override
        public TypeAnswer<R> ask(A1 a1, A2 a2, A3 a3, A4 a4, A5 a5) {
            return this.actorCell.ask(command = new ActorCallCommand(actorCell, () -> caller.call(a1, a2, a3, a4, a5), new LocalTypeAnswer<>(), this.stage), null);
        }

        @Override
        public Stage asking(A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5) {
            this.ask(arg1, arg2, arg3, arg4, arg5);
            return this.stage();
        }
    }


    static class LocalA6Asker<R, A1, A2, A3, A4, A5, A6> extends LocalAsker<R, A6Asker<R, TypeStage<R>, A1, A2, A3, A4, A5, A6>, A6Caller<R, A1, A2, A3, A4, A5, A6>> implements A6Asker<R, TypeStage<R>, A1, A2, A3, A4, A5, A6> {

        LocalA6Asker(ActorCell actorCell, A6Caller caller) {
            this.actorCell = actorCell;
            this.caller = caller;
        }

        LocalA6Asker(ActorCell actorCell, A6Answerer<? extends Actor, R, A1, A2, A3, A4, A5, A6> answerer) {
            this.actorCell = actorCell;
            this.caller = (a1, a2, a3, a4, a5, a6) -> answerer.answer(this.actorCell.getActor(), a1, a2, a3, a4, a5, a6);
        }

        @Override
        public TypeAnswer<R> ask(A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6) {
            return this.actorCell.ask(command = new ActorCallCommand(actorCell, () -> caller.call(a1, a2, a3, a4, a5, a6), new LocalTypeAnswer<>(), this.stage), null);
        }

        @Override
        public Stage asking(A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6) {
            this.ask(arg1, arg2, arg3, arg4, arg5, arg6);
            return this.stage();
        }
    }


    static class LocalA7Asker<R, A1, A2, A3, A4, A5, A6, A7> extends LocalAsker<R, A7Asker<R, TypeStage<R>, A1, A2, A3, A4, A5, A6, A7>, A7Caller<R, A1, A2, A3, A4, A5, A6, A7>> implements A7Asker<R, TypeStage<R>, A1, A2, A3, A4, A5, A6, A7> {

        LocalA7Asker(ActorCell actorCell, A7Caller caller) {
            this.actorCell = actorCell;
            this.caller = caller;
        }

        LocalA7Asker(ActorCell actorCell, A7Answerer<? extends Actor, R, A1, A2, A3, A4, A5, A6, A7> answerer) {
            this.actorCell = actorCell;
            this.caller = (a1, a2, a3, a4, a5, a6, a7) -> answerer.answer(this.actorCell.getActor(), a1, a2, a3, a4, a5, a6, a7);
        }

        @Override
        public TypeAnswer<R> ask(A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6, A7 a7) {
            return this.actorCell.ask(command = new ActorCallCommand(actorCell, () -> caller.call(a1, a2, a3, a4, a5, a6, a7), new LocalTypeAnswer<>(), this.stage), null);
        }

        @Override
        public Stage asking(A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7) {
            this.asking(arg1, arg2, arg3, arg4, arg5, arg6, arg7);
            return this.stage();
        }
    }


}

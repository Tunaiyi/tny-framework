package com.tny.game.actor.local;


import com.tny.game.actor.Actor;
import com.tny.game.actor.Completable;
import com.tny.game.actor.VoidAnswer;
import com.tny.game.actor.invoke.*;
import com.tny.game.actor.stage.Stage;
import com.tny.game.actor.stage.Stages;
import com.tny.game.actor.stage.VoidStage;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
class LocalTeller<TE extends Teller, R> implements Teller<TE>, Completable {

    protected ActorCommand<Void, VoidStage, LocalVoidAnswer> command;

    protected VoidStage stage;

    protected ActorCell actorCell;

    protected R runner;

    @Override
    public boolean isCompleted() {
        return command.isHandled();
    }

    @Override
    @SuppressWarnings("unchecked")
    public TE then(Consumer<VoidStage> initStage) {
        this.stage = stage();
        initStage.accept(this.stage);
        return (TE) this;
    }

    protected VoidStage stage() {
        if (this.stage == null)
            this.stage = Stages.waitUntil(this);
        return this.stage;
    }

    static class LocalUntilTeller extends LocalTeller<A0Teller, Supplier<Boolean>> implements A0Teller {

        protected Supplier<Boolean> supplier;

        LocalUntilTeller(ActorCell actorCell, Predicate<LocalActor> tester) {
            this.actorCell = actorCell;
            this.runner = () -> tester.test(this.actorCell.getActor());
        }

        public LocalUntilTeller(ActorCell actorCell, Supplier<Boolean> supplier) {
            this.actorCell = actorCell;
            this.runner = supplier;
        }

        @Override
        public void tell() {
            this.actorCell.tell(command = new ActorRunUntilCommand<>(actorCell, (actor) -> runner.get(), this.stage), null);
        }


        @Override
        public VoidAnswer tellOf() {
            return this.actorCell.ask(command = new ActorRunUntilCommand<>(actorCell, (actor) -> runner.get(), new LocalVoidAnswer(), this.stage), null);
        }

        @Override
        public Stage telling() {
            this.tell();
            return this.stage();
        }

    }

    static class LocalA0Teller extends LocalTeller<A0Teller, A0Runner> implements A0Teller {

        LocalA0Teller(ActorCell actorCell, A0Runner runner) {
            this.actorCell = actorCell;
            this.runner = runner;
        }

        LocalA0Teller(ActorCell actorCell, A0Acceptor<?> acceptor) {
            this.actorCell = actorCell;
            this.runner = () -> acceptor.accept(this.actorCell.getActor());
        }


        @Override
        public void tell() {
            this.actorCell.tell(command = new ActorRunCommand<>(actorCell, () -> runner.run(), this.stage), null);
        }

        @Override
        public VoidAnswer tellOf() {
            return this.actorCell.ask(command = new ActorRunCommand<>(actorCell, () -> runner.run(), new LocalVoidAnswer(), this.stage), null);
        }

        @Override
        public Stage telling() {
            this.tell();
            return this.stage();
        }

    }

    static class LocalA1Teller<A1> extends LocalTeller<A1Teller<A1>, A1Runner<A1>> implements A1Teller<A1> {

        LocalA1Teller(ActorCell actorCell, A1Runner runner) {
            this.actorCell = actorCell;
            this.runner = runner;
        }

        LocalA1Teller(ActorCell actorCell, A1Acceptor<? extends Actor, A1> acceptor) {
            this.actorCell = actorCell;
            this.runner = (a1) -> acceptor.accept(this.actorCell.getActor(), a1);
        }

        @Override
        public void tell(A1 a1) {
            this.actorCell.tell(command = new ActorRunCommand<>(actorCell, () -> runner.run(a1), this.stage), null);
        }

        @Override
        public VoidAnswer tellOf(A1 a1) {
            return this.actorCell.ask(command = new ActorRunCommand<>(actorCell, () -> runner.run(a1), new LocalVoidAnswer(), this.stage), null);
        }

        @Override
        public Stage telling(A1 arg1) {
            this.tell(arg1);
            return this.stage();
        }
    }

    static class LocalA2Teller<A1, A2> extends LocalTeller<A2Teller<A1, A2>, A2Runner<A1, A2>> implements A2Teller<A1, A2> {

        LocalA2Teller(ActorCell actorCell, A2Runner runner) {
            this.actorCell = actorCell;
            this.runner = runner;
        }

        LocalA2Teller(ActorCell actorCell, A2Acceptor<? extends Actor, A1, A2> acceptor) {
            this.actorCell = actorCell;
            this.runner = (a1, a2) -> acceptor.accept(this.actorCell.getActor(), a1, a2);
        }

        @Override
        public void tell(A1 a1, A2 a2) {
            this.actorCell.tell(command = new ActorRunCommand<>(actorCell, () -> runner.run(a1, a2), this.stage), null);
        }

        @Override
        public VoidAnswer tellOf(A1 a1, A2 a2) {
            return this.actorCell.ask(command = new ActorRunCommand<>(actorCell, () -> runner.run(a1, a2), new LocalVoidAnswer(), this.stage), null);
        }

        @Override
        public Stage telling(A1 arg1, A2 arg2) {
            this.tell(arg1, arg2);
            return this.stage();
        }
    }

    static class LocalA3Teller<A1, A2, A3> extends LocalTeller<A3Teller<A1, A2, A3>, A3Runner<A1, A2, A3>> implements A3Teller<A1, A2, A3> {

        LocalA3Teller(ActorCell actorCell, A3Runner runner) {
            this.actorCell = actorCell;
            this.runner = runner;
        }

        LocalA3Teller(ActorCell actorCell, A3Acceptor<? extends Actor, A1, A2, A3> acceptor) {
            this.actorCell = actorCell;
            this.runner = (a1, a2, a3) -> acceptor.accept(this.actorCell.getActor(), a1, a2, a3);
        }

        @Override
        public void tell(A1 a1, A2 a2, A3 a3) {
            this.actorCell.tell(command = new ActorRunCommand<>(actorCell, () -> runner.run(a1, a2, a3), this.stage), null);
        }

        @Override
        public VoidAnswer tellOf(A1 a1, A2 a2, A3 a3) {
            return this.actorCell.ask(command = new ActorRunCommand<>(actorCell, () -> runner.run(a1, a2, a3), new LocalVoidAnswer(), this.stage), null);
        }

        @Override
        public Stage telling(A1 arg1, A2 arg2, A3 arg3) {
            this.tell(arg1, arg2, arg3);
            return this.stage();
        }
    }


    static class LocalA4Teller<A1, A2, A3, A4> extends LocalTeller<A4Teller<A1, A2, A3, A4>, A4Runner<A1, A2, A3, A4>> implements A4Teller<A1, A2, A3, A4> {

        LocalA4Teller(ActorCell actorCell, A4Runner runner) {
            this.actorCell = actorCell;
            this.runner = runner;
        }

        LocalA4Teller(ActorCell actorCell, A4Acceptor<? extends Actor, A1, A2, A3, A4> acceptor) {
            this.actorCell = actorCell;
            this.runner = (a1, a2, a3, a4) -> acceptor.accept(this.actorCell.getActor(), a1, a2, a3, a4);
        }

        @Override
        public void tell(A1 a1, A2 a2, A3 a3, A4 a4) {
            this.actorCell.tell(command = new ActorRunCommand<>(actorCell, () -> runner.run(a1, a2, a3, a4), this.stage), null);
        }

        @Override
        public VoidAnswer tellOf(A1 a1, A2 a2, A3 a3, A4 a4) {
            return this.actorCell.ask(command = new ActorRunCommand<>(actorCell, () -> runner.run(a1, a2, a3, a4), new LocalVoidAnswer(), this.stage), null);
        }

        @Override
        public Stage telling(A1 arg1, A2 arg2, A3 arg3, A4 arg4) {
            this.tell(arg1, arg2, arg3, arg4);
            return this.stage();
        }
    }


    static class LocalA5Teller<A1, A2, A3, A4, A5> extends LocalTeller<A5Teller<A1, A2, A3, A4, A5>, A5Runner<A1, A2, A3, A4, A5>> implements A5Teller<A1, A2, A3, A4, A5> {

        LocalA5Teller(ActorCell actorCell, A5Runner runner) {
            this.actorCell = actorCell;
            this.runner = runner;
        }

        LocalA5Teller(ActorCell actorCell, A5Acceptor<? extends Actor, A1, A2, A3, A4, A5> acceptor) {
            this.actorCell = actorCell;
            this.runner = (a1, a2, a3, a4, a5) -> acceptor.accept(this.actorCell.getActor(), a1, a2, a3, a4, a5);
        }

        @Override
        public void tell(A1 a1, A2 a2, A3 a3, A4 a4, A5 a5) {
            this.actorCell.tell(command = new ActorRunCommand<>(actorCell, () -> runner.run(a1, a2, a3, a4, a5), this.stage), null);
        }

        @Override
        public VoidAnswer tellOf(A1 a1, A2 a2, A3 a3, A4 a4, A5 a5) {
            return this.actorCell.ask(command = new ActorRunCommand<>(actorCell, () -> runner.run(a1, a2, a3, a4, a5), new LocalVoidAnswer(), this.stage), null);
        }

        @Override
        public Stage telling(A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5) {
            this.tell(arg1, arg2, arg3, arg4, arg5);
            return this.stage();
        }
    }


    static class LocalA6Teller<A1, A2, A3, A4, A5, A6> extends LocalTeller<A6Teller<A1, A2, A3, A4, A5, A6>, A6Runner<A1, A2, A3, A4, A5, A6>> implements A6Teller<A1, A2, A3, A4, A5, A6> {

        LocalA6Teller(ActorCell actorCell, A6Runner runner) {
            this.actorCell = actorCell;
            this.runner = runner;
        }

        LocalA6Teller(ActorCell actorCell, A6Acceptor<? extends Actor, A1, A2, A3, A4, A5, A6> acceptor) {
            this.actorCell = actorCell;
            this.runner = (a1, a2, a3, a4, a5, a6) -> acceptor.accept(this.actorCell.getActor(), a1, a2, a3, a4, a5, a6);
        }

        @Override
        public void tell(A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6) {
            this.actorCell.tell(command = new ActorRunCommand<>(actorCell, () -> runner.run(a1, a2, a3, a4, a5, a6), this.stage), null);
        }

        @Override
        public VoidAnswer tellOf(A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6) {
            return this.actorCell.ask(command = new ActorRunCommand<>(actorCell, () -> runner.run(a1, a2, a3, a4, a5, a6), new LocalVoidAnswer(), this.stage), null);
        }

        @Override
        public Stage telling(A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6) {
            this.tell(arg1, arg2, arg3, arg4, arg5, arg6);
            return this.stage();
        }
    }


    static class LocalA7Teller<A1, A2, A3, A4, A5, A6, A7> extends LocalTeller<A7Teller<A1, A2, A3, A4, A5, A6, A7>, A7Runner<A1, A2, A3, A4, A5, A6, A7>> implements A7Teller<A1, A2, A3, A4, A5, A6, A7> {

        LocalA7Teller(ActorCell actorCell, A7Runner runner) {
            this.actorCell = actorCell;
            this.runner = runner;
        }

        LocalA7Teller(ActorCell actorCell, A7Acceptor<? extends Actor, A1, A2, A3, A4, A5, A6, A7> acceptor) {
            this.actorCell = actorCell;
            this.runner = (a1, a2, a3, a4, a5, a6, a7) -> acceptor.accept(this.actorCell.getActor(), a1, a2, a3, a4, a5, a6, a7);
        }

        @Override
        public void tell(A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6, A7 a7) {
            this.actorCell.tell(command = new ActorRunCommand<>(actorCell, () -> runner.run(a1, a2, a3, a4, a5, a6, a7), this.stage), null);
        }

        @Override
        public VoidAnswer tellOf(A1 a1, A2 a2, A3 a3, A4 a4, A5 a5, A6 a6, A7 a7) {
            return this.actorCell.ask(command = new ActorRunCommand<>(actorCell, () -> runner.run(a1, a2, a3, a4, a5, a6, a7), new LocalVoidAnswer(), this.stage), null);
        }

        @Override
        public Stage telling(A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6, A7 arg7) {
            this.tell(arg1, arg2, arg3, arg4, arg5, arg6, arg7);
            return this.stage();
        }
    }


}

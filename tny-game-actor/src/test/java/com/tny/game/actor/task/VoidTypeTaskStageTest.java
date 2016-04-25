package com.tny.game.actor.task;

import com.tny.game.actor.task.invok.CatcherRun;
import com.tny.game.actor.task.invok.RunDone;
import com.tny.game.actor.task.invok.SupplyDone;
import org.jmock.Expectations;
import org.junit.Test;

import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.Assert.*;

/**
 * Created by Kun Yang on 16/1/25.
 */
@SuppressWarnings("unchecked")
public class VoidTypeTaskStageTest extends TaskStageTestUnits {

    @Test
    public void testJoinRun() throws Exception {
        final Runnable fn = context.mock(Runnable.class);
        context.checking(new Expectations() {{
            exactly(3).of(fn).run();
        }});
        checkStage(
                Stages.run(fn::run)
                        .joinSupply(() -> Stages.run(fn::run))
                        .thenRun(fn::run)
                , true
        );
        context.assertIsSatisfied();
    }


    @Test
    public void testJoinSupply() throws Exception {
        final Runnable fn = context.mock(Runnable.class);
        final Supplier<String> tfn = context.mock(Supplier.class);
        context.checking(new Expectations() {{
            oneOf(fn).run();
            oneOf(tfn).get();
            will(returnValue(value));
            oneOf(fn).run();
        }});
        checkStage(
                Stages.run(fn::run)
                        .joinSupply(() -> Stages.supply(tfn::get))
                        .thenRun(fn::run)
                , true
        );
        context.assertIsSatisfied();
    }


    @Test
    public void testThenRun() throws Exception {
        final Runnable fn = context.mock(Runnable.class);
        context.checking(new Expectations() {{
            exactly(2).of(fn).run();
        }});
        checkStage(
                Stages.run(fn::run)
                        .thenRun(fn::run)
                , true
        );
        context.assertIsSatisfied();
    }


    @Test
    public void testThenSupply() throws Exception {
        final Runnable fn = context.mock(Runnable.class);
        final Supplier<String> tfn = context.mock(Supplier.class);
        context.checking(new Expectations() {{
            exactly(1).of(fn).run();
            exactly(1).of(tfn).get();
            will(returnValue(value));
        }});
        checkStage(
                Stages.run(fn::run)
                        .thenSupply(tfn::get)
                , true, value
        );
        context.assertIsSatisfied();
    }

    @Test
    public void testDoneRun() throws Exception {
        final Runnable fn = context.mock(Runnable.class);
        final RunDone tfn = context.mock(RunDone.class);


        //正常处理
        context.checking(new Expectations() {{
            oneOf(fn).run();
            oneOf(tfn).run(true, null);
        }});
        checkStage(
                Stages.run(fn::run)
                        .doneRun(tfn)
                , true
        );
        context.assertIsSatisfied();

        //异常回复

        context.checking(new Expectations() {{
            oneOf(tfn).run(false, exception);
        }});
        checkStage(
                Stages.run(() -> {
                    throw exception;
                })
                        .doneRun(tfn)
                , true
        );
        context.assertIsSatisfied();


        //异常继续抛出
        context.checking(new Expectations() {{
            oneOf(tfn).run(false, exception);
            will(throwException(exception));
        }});
        checkStage(
                Stages.run(() -> {
                    throw exception;
                })
                        .doneRun(tfn)
                , false
        );
        context.assertIsSatisfied();

        //正常 处理抛出
        context.checking(new Expectations() {{
            oneOf(fn).run();
            oneOf(tfn).run(true, null);
            will(throwException(exception));
        }});
        checkStage(
                Stages.run(fn::run)
                        .doneRun(tfn)
                , false
        );
        context.assertIsSatisfied();
    }


    @Test
    public void testDoneSupply() throws Exception {
        final Runnable fn = context.mock(Runnable.class);
        final SupplyDone<String> tfn = context.mock(SupplyDone.class);


        //正常处理
        context.checking(new Expectations() {{
            oneOf(fn).run();
            oneOf(tfn).handle(true, null);
            will(returnValue(value));
        }});
        checkStage(
                Stages.run(fn::run)
                        .doneSupply(tfn)
                , true, value
        );
        context.assertIsSatisfied();

        //异常回复

        context.checking(new Expectations() {{
            oneOf(tfn).handle(false, exception);
            will(returnValue(value));
        }});
        checkStage(
                Stages.run(() -> {
                    throw exception;
                })
                        .doneSupply(tfn)
                , true, value
        );
        context.assertIsSatisfied();


        //异常继续抛出
        context.checking(new Expectations() {{
            oneOf(tfn).handle(false, exception);
            will(throwException(exception));
        }});
        checkStage(
                Stages.run(() -> {
                    throw exception;
                })
                        .doneSupply(tfn)
                , false
        );
        context.assertIsSatisfied();

        //正常 处理抛出
        context.checking(new Expectations() {{
            oneOf(fn).run();
            oneOf(tfn).handle(true, null);
            will(throwException(exception));
        }});
        checkStage(
                Stages.run(fn::run)
                        .doneSupply(tfn)
                , false, null
        );
        context.assertIsSatisfied();
    }

    @Test
    public void testThenThrow() throws Exception {
        final Runnable fn = context.mock(Runnable.class);
        final CatcherRun tfn = context.mock(CatcherRun.class);


        //正常处理
        context.checking(new Expectations() {{
            oneOf(fn).run();
            never(tfn).catchThrow(null);
        }});
        checkStage(
                Stages.run(fn::run)
                        .thenThrow(tfn)
                , true
        );
        context.assertIsSatisfied();

        //异常回复

        context.checking(new Expectations() {{
            oneOf(tfn).catchThrow(exception);
        }});
        checkStage(
                Stages.run(() -> {
                    throw exception;
                })
                        .thenThrow(tfn)
                , true
        );
        context.assertIsSatisfied();


        //异常继续抛出
        context.checking(new Expectations() {{
            oneOf(tfn).catchThrow(exception);
            will(throwException(exception));
        }});
        checkStage(
                Stages.run(() -> {
                    throw exception;
                })
                        .thenThrow(tfn)
                , false
        );
        context.assertIsSatisfied();

    }

    @Test
    public void testAwaitRun() throws Exception {
        final Runnable fn = context.mock(Runnable.class);
        context.checking(new Expectations() {{
            exactly(2).of(fn).run();
        }});
        long time = System.currentTimeMillis();
        checkStage(
                Stages.run(fn::run)
                        .awaitRun(Stages.time(TIME_100))
                        .thenRun(fn::run)
                , true
        );
        assertTrue(System.currentTimeMillis() >= time + TIME_100.toMillis());
        context.assertIsSatisfied();
    }

    @Test
    public void testAwaitRun1() throws Exception {
        final Runnable fn = context.mock(Runnable.class);
        context.checking(new Expectations() {{
            exactly(2).of(fn).run();
        }});
        long time = System.currentTimeMillis();
        checkStage(
                Stages.run(fn::run)
                        .awaitRun(Stages.time(TIME_100), TIME_200)
                        .thenRun(fn::run)
                , true
        );
        assertTrue(System.currentTimeMillis() >= time + TIME_100.toMillis());
        context.assertIsSatisfied();

        context.checking(new Expectations() {{
            exactly(1).of(fn).run();
        }});
        checkStage(
                Stages.run(fn::run)
                        .awaitRun(Stages.time(TIME_200), TIME_100)
                        .thenRun(fn::run)
                , false
        );
        context.assertIsSatisfied();
    }


    @Test
    public void testAwaitSupply() throws Exception {
        final Runnable fn = context.mock(Runnable.class);
        final Function<String, String> cfn = context.mock(Function.class);
        context.checking(new Expectations() {{
            exactly(1).of(fn).run();
            oneOf(cfn).apply(value);
            will(returnValue(value));
        }});
        long time = System.currentTimeMillis();
        checkStage(
                Stages.run(fn::run)
                        .awaitSupply(Stages.time(value, TIME_100))
                        .thenApply(cfn)
                , true, value
        );
        assertTrue(System.currentTimeMillis() >= time + TIME_100.toMillis());
        context.assertIsSatisfied();

    }

    @Test
    public void testAwaitSupply1() throws Exception {

        final Runnable fn = context.mock(Runnable.class);
        final Function<String, String> cfn = context.mock(Function.class);
        context.checking(new Expectations() {{
            exactly(1).of(fn).run();
            oneOf(cfn).apply(value);
            will(returnValue(value));
        }});
        long time = System.currentTimeMillis();
        checkStage(
                Stages.run(fn::run)
                        .awaitSupply(Stages.time(value, TIME_100), TIME_200)
                        .thenApply(cfn)
                , true, value
        );
        assertTrue(System.currentTimeMillis() >= time + TIME_100.toMillis());
        context.assertIsSatisfied();

        context.checking(new Expectations() {{
            exactly(1).of(fn).run();
        }});
        checkStage(
                Stages.run(fn::run)
                        .awaitSupply(Stages.time(value, TIME_200), TIME_100)
                        .thenRun(fn::run)
                , false
        );
        context.assertIsSatisfied();
    }

}
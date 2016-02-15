package com.tny.game.actor.task;

import com.tny.game.actor.task.invok.AcceptDone;
import com.tny.game.actor.task.invok.ApplyDone;
import com.tny.game.actor.task.invok.CatcherSupplier;
import com.tny.game.actor.task.invok.TaskAcceptDone;
import com.tny.game.actor.task.invok.TaskApplyDone;
import com.tny.game.actor.task.invok.TaskCatcherSupplier;
import com.tny.game.actor.task.invok.TaskConsumer;
import com.tny.game.actor.task.invok.TaskFunction;
import com.tny.game.base.item.Do;
import org.jmock.Expectations;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.Assert.*;

/**
 * 有返回值的Stage测试类
 * Created by Kun Yang on 16/2/2.
 */
@SuppressWarnings("unchecked")
public class TaskStageTest extends TaskStageTestUnits {

    @Test
    public void testJoinApply() throws Exception {
        final Supplier<String> fn = context.mock(Supplier.class);
        final Function<String, TaskStage<String>> tfn = context.mock(Function.class);
        context.checking(new Expectations() {{
            oneOf(fn).get();
            will(returnValue(value));
            oneOf(tfn).apply(value);
            will(returnValue(Stages.supply(() -> other)));
        }});
        checkStage(
                Stages.supply(fn)
                        .joinApply(tfn)
                , true, other
        );
        context.assertIsSatisfied();
    }

    @Test
    public void testJoinApplyWith() throws Exception {
        final Supplier<String> fn = context.mock(Supplier.class);
        final TaskFunction<String, TaskStage<String>> tfn = context.mock(TaskFunction.class);
        context.checking(new Expectations() {{
            oneOf(fn).get();
            will(returnValue(value));
            oneOf(tfn).apply(value, null);
            will(returnValue(Stages.supply(() -> other)));
        }});
        checkStage(
                Stages.supply(fn)
                        .joinApplyWith(tfn)
                , true, other
        );
        context.assertIsSatisfied();
    }

    @Test
    public void testJoinAccept() throws Exception {
        final Runnable runFn = context.mock(Runnable.class);
        final Supplier<String> fn = context.mock(Supplier.class);
        final Function<String, TaskStage<String>> tfn = context.mock(Function.class);
        context.checking(new Expectations() {{
            oneOf(fn).get();
            will(returnValue(value));
            oneOf(tfn).apply(value);
            will(returnValue(Stages.run(runFn)));
            oneOf(runFn).run();
        }});
        checkStage(
                Stages.supply(fn)
                        .joinApply(tfn)
                , true
        );
        context.assertIsSatisfied();
    }

    @Test
    public void testJoinAcceptWith() throws Exception {
        final Runnable runFn = context.mock(Runnable.class);
        final Supplier<String> fn = context.mock(Supplier.class);
        final TaskFunction<String, TaskStage<String>> tfn = context.mock(TaskFunction.class);
        context.checking(new Expectations() {{
            oneOf(fn).get();
            will(returnValue(value));
            oneOf(tfn).apply(value, null);
            will(returnValue(Stages.run(runFn)));
            oneOf(runFn).run();
        }});
        checkStage(
                Stages.supply(fn)
                        .joinApplyWith(tfn)
                , true
        );
        context.assertIsSatisfied();
    }

    @Test
    public void testThenApply() throws Exception {
        final Supplier<String> fn = context.mock(Supplier.class);
        final Function<String, String> tfn = context.mock(Function.class);
        context.checking(new Expectations() {{
            oneOf(fn).get();
            will(returnValue(value));
            oneOf(tfn).apply(value);
            will(returnValue(other));
        }});
        checkStage(
                Stages.supply(fn)
                        .thenApply(tfn)
                , true, other
        );
        context.assertIsSatisfied();
    }

    @Test
    public void testThenApplyWith() throws Exception {
        final Supplier<String> fn = context.mock(Supplier.class);
        final TaskFunction<String, String> tfn = context.mock(TaskFunction.class);
        context.checking(new Expectations() {{
            oneOf(fn).get();
            will(returnValue(value));
            oneOf(tfn).apply(value, null);
            will(returnValue(other));
        }});
        checkStage(
                Stages.supply(fn)
                        .thenApplyWith(tfn)
                , true, other
        );
        context.assertIsSatisfied();
    }

    @Test
    public void testThenAccept() throws Exception {
        final Supplier<String> fn = context.mock(Supplier.class);
        final Consumer<String> tfn = context.mock(Consumer.class);
        context.checking(new Expectations() {{
            oneOf(fn).get();
            will(returnValue(value));
            oneOf(tfn).accept(value);
        }});
        checkStage(
                Stages.supply(fn)
                        .thenAccept(tfn)
                , true
        );
        context.assertIsSatisfied();

    }

    @Test
    public void testThenAcceptWith() throws Exception {
        final Supplier<String> fn = context.mock(Supplier.class);
        final TaskConsumer<String> tfn = context.mock(TaskConsumer.class);
        context.checking(new Expectations() {{
            oneOf(fn).get();
            will(returnValue(value));
            oneOf(tfn).accept(value, null);
        }});
        checkStage(
                Stages.supply(fn)
                        .thenAcceptWith(tfn)
                , true
        );
        context.assertIsSatisfied();
    }

    @Test
    public void testDoneApply() throws Exception {
        final Supplier<String> fn = context.mock(Supplier.class);
        final ApplyDone<String, String> tfn = context.mock(ApplyDone.class);


        //正常处理
        context.checking(new Expectations() {{
            oneOf(fn).get();
            will(returnValue(value));
            oneOf(tfn).handle(true, value, null);
            will(returnValue(other));
        }});
        checkStage(
                Stages.supply(fn)
                        .doneApply(tfn)
                , true, other
        );
        context.assertIsSatisfied();

        //异常回复

        context.checking(new Expectations() {{
            oneOf(fn).get();
            will(throwException(exception));
            oneOf(tfn).handle(false, null, exception);
            will(returnValue(value));
        }});
        checkStage(
                Stages.supply(fn).doneApply(tfn)
                , true, value
        );
        context.assertIsSatisfied();


        //异常继续抛出
        context.checking(new Expectations() {{
            oneOf(fn).get();
            will(throwException(exception));
            oneOf(tfn).handle(false, null, exception);
            will(throwException(exception));
        }});
        checkStage(
                Stages.supply(fn).doneApply(tfn)
                , false
        );
        context.assertIsSatisfied();

        //正常 处理抛出
        context.checking(new Expectations() {{
            oneOf(fn).get();
            will(returnValue(value));
            oneOf(tfn).handle(true, value, null);
            will(throwException(exception));
        }});
        checkStage(
                Stages.supply(fn)
                        .doneApply(tfn)
                , false
        );
        context.assertIsSatisfied();
    }

    @Test
    public void testDoneApplyWith() throws Exception {
        final Supplier<String> fn = context.mock(Supplier.class);
        final TaskApplyDone<String, String> tfn = context.mock(TaskApplyDone.class);


        //正常处理
        context.checking(new Expectations() {{
            oneOf(fn).get();
            will(returnValue(value));
            oneOf(tfn).handle(true, value, null, null);
            will(returnValue(other));
        }});
        checkStage(
                Stages.supply(fn)
                        .doneApplyWith(tfn)
                , true, other
        );
        context.assertIsSatisfied();

        //异常回复

        context.checking(new Expectations() {{
            oneOf(fn).get();
            will(throwException(exception));
            oneOf(tfn).handle(false, null, exception, null);
            will(returnValue(value));
        }});
        checkStage(
                Stages.supply(fn).doneApplyWith(tfn)
                , true, value
        );
        context.assertIsSatisfied();


        //异常继续抛出
        context.checking(new Expectations() {{
            oneOf(fn).get();
            will(throwException(exception));
            oneOf(tfn).handle(false, null, exception, null);
            will(throwException(exception));
        }});
        checkStage(
                Stages.supply(fn).doneApplyWith(tfn)
                , false
        );
        context.assertIsSatisfied();

        //正常 处理抛出
        context.checking(new Expectations() {{
            oneOf(fn).get();
            will(returnValue(value));
            oneOf(tfn).handle(true, value, null, null);
            will(throwException(exception));
        }});
        checkStage(
                Stages.supply(fn)
                        .doneApplyWith(tfn)
                , false
        );
        context.assertIsSatisfied();
    }

    @Test
    public void testDoneAccept() throws Exception {
        final Supplier<String> fn = context.mock(Supplier.class);
        final AcceptDone<String> tfn = context.mock(AcceptDone.class);


        //正常处理
        context.checking(new Expectations() {{
            oneOf(fn).get();
            will(returnValue(value));
            oneOf(tfn).handle(true, value, null);
        }});
        checkStage(
                Stages.supply(fn)
                        .doneAccept(tfn)
                , true
        );
        context.assertIsSatisfied();

        //异常回复

        context.checking(new Expectations() {{
            oneOf(fn).get();
            will(throwException(exception));
            oneOf(tfn).handle(false, null, exception);
        }});
        checkStage(
                Stages.supply(fn).doneAccept(tfn)
                , true
        );
        context.assertIsSatisfied();


        //异常继续抛出
        context.checking(new Expectations() {{
            oneOf(fn).get();
            will(throwException(exception));
            oneOf(tfn).handle(false, null, exception);
            will(throwException(exception));
        }});
        checkStage(
                Stages.supply(fn).doneAccept(tfn)
                , false
        );
        context.assertIsSatisfied();

        //正常 处理抛出
        context.checking(new Expectations() {{
            oneOf(fn).get();
            will(returnValue(value));
            oneOf(tfn).handle(true, value, null);
            will(throwException(exception));
        }});
        checkStage(
                Stages.supply(fn)
                        .doneAccept(tfn)
                , false
        );
        context.assertIsSatisfied();
    }

    @Test
    public void testDoneAcceptWith() throws Exception {
        final Supplier<String> fn = context.mock(Supplier.class);
        final TaskAcceptDone<String> tfn = context.mock(TaskAcceptDone.class);


        //正常处理
        context.checking(new Expectations() {{
            oneOf(fn).get();
            will(returnValue(value));
            oneOf(tfn).handle(true, value, null, null);
        }});
        checkStage(
                Stages.supply(fn)
                        .doneAcceptWith(tfn)
                , true
        );
        context.assertIsSatisfied();

        //异常回复

        context.checking(new Expectations() {{
            oneOf(fn).get();
            will(throwException(exception));
            oneOf(tfn).handle(false, null, exception, null);
        }});
        checkStage(
                Stages.supply(fn).doneAcceptWith(tfn)
                , true
        );
        context.assertIsSatisfied();


        //异常继续抛出
        context.checking(new Expectations() {{
            oneOf(fn).get();
            will(throwException(exception));
            oneOf(tfn).handle(false, null, exception, null);
            will(throwException(exception));
        }});
        checkStage(
                Stages.supply(fn).doneAcceptWith(tfn)
                , false
        );
        context.assertIsSatisfied();

        //正常 处理抛出
        context.checking(new Expectations() {{
            oneOf(fn).get();
            will(returnValue(value));
            oneOf(tfn).handle(true, value, null, null);
            will(throwException(exception));
        }});
        checkStage(
                Stages.supply(fn)
                        .doneAcceptWith(tfn)
                , false
        );
        context.assertIsSatisfied();

    }

    @Test
    public void testThenThrow() throws Exception {
        final Supplier<String> fn = context.mock(Supplier.class);
        final CatcherSupplier<String> tfn = context.mock(CatcherSupplier.class);


        //正常处理
        context.checking(new Expectations() {{
            oneOf(fn).get();
            will(returnValue(value));
            never(tfn).catchThrow(null);
        }});
        checkStage(
                Stages.supply(fn)
                        .thenThrow(tfn)
                , true, value
        );
        context.assertIsSatisfied();

        //异常回复
        context.checking(new Expectations() {{
            oneOf(fn).get();
            will(throwException(exception));
            oneOf(tfn).catchThrow(exception);
            will(returnValue(other));
        }});
        checkStage(
                Stages.supply(fn)
                        .thenThrow(tfn)
                , true, other
        );
        context.assertIsSatisfied();


        //异常继续抛出
        context.checking(new Expectations() {{
            oneOf(fn).get();
            will(throwException(exception));
            oneOf(tfn).catchThrow(exception);
            will(throwException(exception));
        }});
        checkStage(
                Stages.supply(fn)
                        .thenThrow(tfn)
                , false
        );
        context.assertIsSatisfied();
    }

    @Test
    public void testThenThrow1() throws Exception {

        final Supplier<String> fn = context.mock(Supplier.class);
        final TaskCatcherSupplier<String> tfn = context.mock(TaskCatcherSupplier.class);


        //正常处理
        context.checking(new Expectations() {{
            oneOf(fn).get();
            will(returnValue(value));
            never(tfn).catchThrow(null, null);
        }});
        checkStage(
                Stages.supply(fn)
                        .thenThrow(tfn)
                , true, value
        );
        context.assertIsSatisfied();

        //异常回复
        context.checking(new Expectations() {{
            oneOf(fn).get();
            will(throwException(exception));
            oneOf(tfn).catchThrow(exception, null);
            will(returnValue(other));
        }});
        checkStage(
                Stages.supply(fn)
                        .thenThrow(tfn)
                , true, other
        );
        context.assertIsSatisfied();


        //异常继续抛出
        context.checking(new Expectations() {{
            oneOf(fn).get();
            will(throwException(exception));
            oneOf(tfn).catchThrow(exception, null);
            will(throwException(exception));
        }});
        checkStage(
                Stages.supply(fn)
                        .thenThrow(tfn)
                , false
        );
        context.assertIsSatisfied();
    }

    @Test
    public void testAwaitApply() throws Exception {
        final Supplier<String> fn = context.mock(Supplier.class);
        final Function<String, String> tfn = context.mock(Function.class);
        context.checking(new Expectations() {{
            oneOf(fn).get();
            will(returnValue(value));
            allowing(tfn).apply(other);
            will(returnValue(other));
        }});
        long time = System.currentTimeMillis();
        AtomicInteger times = new AtomicInteger(0);
        checkStage(
                Stages.supply(fn)
                        .awaitApply((value) -> {
                            assertEquals(value, TaskStageTestUnits.value);
                            times.getAndIncrement();
                            if (System.currentTimeMillis() < time + TIME_100.toMillis())
                                return Do.fail();
                            else
                                return Do.succ(other);
                        })
                        .thenApply(tfn)
                , true, other
        );
        assertTrue(times.get() > 0);
        assertTrue(System.currentTimeMillis() >= time + TIME_100.toMillis());
        context.assertIsSatisfied();
    }

    @Test
    public void testAwaitApply1() throws Exception {
        final Supplier<String> fn = context.mock(Supplier.class);
        final Function<String, String> tfn = context.mock(Function.class);
        context.checking(new Expectations() {{
            oneOf(fn).get();
            will(returnValue(value));
            allowing(tfn).apply(other);
            will(returnValue(other));
        }});
        long time1 = System.currentTimeMillis();
        AtomicInteger times1 = new AtomicInteger(0);
        checkStage(
                Stages.supply(fn)
                        .awaitApply((value) -> {
                            assertEquals(value, TaskStageTestUnits.value);
                            times1.getAndIncrement();
                            if (System.currentTimeMillis() < time1 + TIME_100.toMillis())
                                return Do.fail();
                            else
                                return Do.succ(other);
                        }, TIME_200)
                        .thenApply(tfn)
                , true, other
        );
        assertTrue(times1.get() > 0);
        assertTrue(System.currentTimeMillis() >= time1 + TIME_100.toMillis());
        context.assertIsSatisfied();

        context.checking(new Expectations() {{
            oneOf(fn).get();
            will(returnValue(value));
        }});
        long time2 = System.currentTimeMillis();
        AtomicInteger times2 = new AtomicInteger(0);
        checkStage(
                Stages.supply(fn)
                        .awaitApply((value) -> {
                            times2.getAndIncrement();
                            if (System.currentTimeMillis() < time2 + TIME_200.toMillis())
                                return Do.fail();
                            else
                                return Do.succ(other);
                        }, TIME_100)
                        .thenApply(tfn)
                , false
        );
        assertTrue(times2.get() > 0);
        context.assertIsSatisfied();

    }

    @Test
    public void testAwaitApplyWith() throws Exception {
        final Supplier<String> fn = context.mock(Supplier.class);
        final Function<String, String> tfn = context.mock(Function.class);
        context.checking(new Expectations() {{
            oneOf(fn).get();
            will(returnValue(value));
            oneOf(tfn).apply(other);
            will(returnValue(other));
        }});
        long time = System.currentTimeMillis();
        AtomicInteger times = new AtomicInteger(0);
        checkStage(
                Stages.supply(fn)
                        .awaitApplyWith((value, context) -> {
                            assertNull(context);
                            assertEquals(value, TaskStageTestUnits.value);
                            times.getAndIncrement();
                            if (System.currentTimeMillis() < time + TIME_100.toMillis())
                                return Do.fail();
                            else
                                return Do.succ(other);
                        })
                        .thenApply(tfn)
                , true, other
        );
        assertTrue(times.get() > 0);
        assertTrue(System.currentTimeMillis() >= time + TIME_100.toMillis());
        context.assertIsSatisfied();
    }

    @Test
    public void testAwaitApplyWith1() throws Exception {

        final Supplier<String> fn = context.mock(Supplier.class);
        final Function<String, String> tfn = context.mock(Function.class);
        context.checking(new Expectations() {{
            oneOf(fn).get();
            will(returnValue(value));
            oneOf(tfn).apply(other);
            will(returnValue(other));
        }});
        long time1 = System.currentTimeMillis();
        AtomicInteger times1 = new AtomicInteger(0);
        checkStage(
                Stages.supply(fn)
                        .awaitApplyWith((value, context) -> {
                            assertNull(context);
                            assertEquals(value, TaskStageTestUnits.value);
                            times1.getAndIncrement();
                            if (System.currentTimeMillis() < time1 + TIME_100.toMillis())
                                return Do.fail();
                            else
                                return Do.succ(other);
                        }, TIME_200)
                        .thenApply(tfn)
                , true, other
        );
        assertTrue(times1.get() > 0);
        assertTrue(System.currentTimeMillis() >= time1 + TIME_100.toMillis());
        context.assertIsSatisfied();

        context.checking(new Expectations() {{
            oneOf(fn).get();
            will(returnValue(value));
        }});
        long time2 = System.currentTimeMillis();
        AtomicInteger times2 = new AtomicInteger(0);
        checkStage(
                Stages.supply(fn)
                        .awaitApplyWith((value, context) -> {
                            assertNull(context);
                            assertEquals(value, TaskStageTestUnits.value);
                            times2.getAndIncrement();
                            if (System.currentTimeMillis() < time2 + TIME_200.toMillis())
                                return Do.fail();
                            else
                                return Do.succ(other);
                        }, TIME_100)
                        .thenApply(tfn)
                , false
        );
        assertTrue(times2.get() > 0);
        context.assertIsSatisfied();
    }

    @Test
    public void testAwaitAccept() throws Exception {
        final Supplier<String> fn = context.mock(Supplier.class);
        final Runnable tfn = context.mock(Runnable.class);
        context.checking(new Expectations() {{
            oneOf(fn).get();
            will(returnValue(value));
            oneOf(tfn).run();
        }});
        long time = System.currentTimeMillis();
        AtomicInteger times = new AtomicInteger(0);
        checkStage(
                Stages.supply(fn)
                        .awaitAccept((value) -> {
                            assertEquals(value, TaskStageTestUnits.value);
                            times.getAndIncrement();
                            return System.currentTimeMillis() >= time + TIME_100.toMillis();
                        })
                        .thenRun(tfn)
                , true
        );
        assertTrue(times.get() > 0);
        assertTrue(System.currentTimeMillis() >= time + TIME_100.toMillis());
        context.assertIsSatisfied();

    }

    @Test
    public void testAwaitAccept1() throws Exception {
        final Supplier<String> fn = context.mock(Supplier.class);
        final Runnable tfn = context.mock(Runnable.class);
        context.checking(new Expectations() {{
            oneOf(fn).get();
            will(returnValue(value));
            oneOf(tfn).run();
        }});
        long time1 = System.currentTimeMillis();
        AtomicInteger times1 = new AtomicInteger(0);
        checkStage(
                Stages.supply(fn)
                        .awaitAccept((value) -> {
                            assertEquals(value, TaskStageTestUnits.value);
                            times1.getAndIncrement();
                            return System.currentTimeMillis() >= time1 + TIME_100.toMillis();
                        }, TIME_200)
                        .thenRun(tfn)
                , true
        );
        assertTrue(times1.get() > 0);
        assertTrue(System.currentTimeMillis() >= time1 + TIME_100.toMillis());
        context.assertIsSatisfied();

        context.checking(new Expectations() {{
            oneOf(fn).get();
            will(returnValue(value));
        }});
        long time2 = System.currentTimeMillis();
        AtomicInteger times2 = new AtomicInteger(0);
        checkStage(
                Stages.supply(fn)
                        .awaitAccept((value) -> {
                            times2.getAndIncrement();
                            return System.currentTimeMillis() >= time2 + TIME_200.toMillis();
                        }, TIME_100)
                        .thenRun(tfn)
                , false
        );
        assertTrue(times2.get() > 0);
        context.assertIsSatisfied();
    }

    @Test
    public void testAwaitAcceptWith() throws Exception {
        final Supplier<String> fn = context.mock(Supplier.class);
        final Runnable tfn = context.mock(Runnable.class);
        context.checking(new Expectations() {{
            oneOf(fn).get();
            will(returnValue(value));
            oneOf(tfn).run();
        }});
        long time = System.currentTimeMillis();
        AtomicInteger times = new AtomicInteger(0);
        checkStage(
                Stages.supply(fn)
                        .awaitAcceptWith((value, context) -> {
                            assertNull(context);
                            assertEquals(value, TaskStageTestUnits.value);
                            times.getAndIncrement();
                            return System.currentTimeMillis() >= time + TIME_100.toMillis();
                        })
                        .thenRun(tfn)
                , true
        );
        assertTrue(times.get() > 0);
        assertTrue(System.currentTimeMillis() >= time + TIME_100.toMillis());
        context.assertIsSatisfied();
    }

    @Test
    public void testAwaitAcceptWith1() throws Exception {
        final Supplier<String> fn = context.mock(Supplier.class);
        final Runnable tfn = context.mock(Runnable.class);
        context.checking(new Expectations() {{
            oneOf(fn).get();
            will(returnValue(value));
            oneOf(tfn).run();
        }});
        long time1 = System.currentTimeMillis();
        AtomicInteger times1 = new AtomicInteger(0);
        checkStage(
                Stages.supply(fn)
                        .awaitAcceptWith((value, context) -> {
                            assertNull(context);
                            assertEquals(value, TaskStageTestUnits.value);
                            times1.getAndIncrement();
                            return System.currentTimeMillis() >= time1 + TIME_100.toMillis();
                        }, TIME_200)
                        .thenRun(tfn)
                , true
        );
        assertTrue(times1.get() > 0);
        assertTrue(System.currentTimeMillis() >= time1 + TIME_100.toMillis());
        context.assertIsSatisfied();

        context.checking(new Expectations() {{
            oneOf(fn).get();
            will(returnValue(value));
        }});
        long time2 = System.currentTimeMillis();
        AtomicInteger times2 = new AtomicInteger(0);
        checkStage(
                Stages.supply(fn)
                        .awaitAcceptWith((value, context) -> {
                            assertNull(context);
                            assertEquals(value, TaskStageTestUnits.value);
                            times2.getAndIncrement();
                            return System.currentTimeMillis() >= time2 + TIME_200.toMillis();
                        }, TIME_100)
                        .thenRun(tfn)
                , false
        );
        assertTrue(times2.get() > 0);
        context.assertIsSatisfied();
    }
}
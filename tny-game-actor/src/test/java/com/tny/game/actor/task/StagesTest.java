package com.tny.game.actor.task;

import com.google.common.base.Supplier;
import com.tny.game.common.utils.Do;
import com.tny.game.common.utils.Done;
import org.jmock.Expectations;
import org.junit.Test;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

import static org.junit.Assert.*;

/**
 * Created by Kun Yang on 16/1/25.
 */
@SuppressWarnings("unchecked")
public class StagesTest extends TaskStageTestUnits {

    @Test
    public void testRun() throws Exception {
        final Runnable fn = context.mock(Runnable.class);
        context.checking(new Expectations() {{
            oneOf(fn).run();
        }});
        checkStage(
                Stages.run(fn::run)
                , true
        );
        context.assertIsSatisfied();
    }


    @Test
    public void testSupply() throws Exception {
        final Supplier<String> fn = context.mock(Supplier.class);

        context.checking(new Expectations() {{
            oneOf(fn).get();
            will(returnValue(value));
        }});
        checkStage(
                Stages.supply(fn::get)
                , true, value
        );
        context.assertIsSatisfied();
    }

    @Test
    public void testAwaitRun() throws Exception {
        final BooleanSupplier fn = context.mock(BooleanSupplier.class);
        context.checking(new Expectations() {{
            exactly(3).of(fn).getAsBoolean();
            will(onConsecutiveCalls(
                    returnValue(false),
                    returnValue(false),
                    returnValue(true)
            ));
        }});
        checkStage(
                Stages.awaitRun(fn::getAsBoolean)
                , true
        );
        context.assertIsSatisfied();
    }

    @Test
    public void testAwaitRun1() throws Exception {
        VoidTaskStage stage = checkStage(
                Stages.awaitRun(() -> false, Duration.ofMillis(10))
                , false
        );
        assertNotNull(stage.getCause());
        context.assertIsSatisfied();
    }

    @Test
    public void testAwaitSupply() throws Exception {
        final Supplier<Done<String>> fn = context.mock(Supplier.class);
        context.checking(new Expectations() {{
            exactly(3).of(fn).get();
            will(onConsecutiveCalls(
                    returnValue(Do.fail()),
                    returnValue(Do.fail()),
                    returnValue(Do.succ(value))
            ));
        }});
        checkStage(
                Stages.awaitSupply(fn::get)
                , true, value
        );
        context.assertIsSatisfied();

    }

    @Test
    public void testAwaitSupply1() throws Exception {
        TypeTaskStage<String> stage = checkStage(
                Stages.awaitSupply(Do::fail, TIME_100)
                , false, null
        );
        assertNotNull(stage.getCause());
        context.assertIsSatisfied();
    }

    @Test
    public void yieldForFuture() throws Exception {
        final CompletableFuture<String> future = new CompletableFuture<>();
        long time = System.currentTimeMillis();
        scheduled.schedule(() -> future.complete(value), TIME_100.toMillis(), TimeUnit.MILLISECONDS);

        checkStage(
                Stages.yieldForFuture(future)
                , true, value
        );

        assertTrue(System.currentTimeMillis() >= time + TIME_100.toMillis());
        context.assertIsSatisfied();

    }

    @Test
    public void yieldForFuture1() throws Exception {
        final CompletableFuture<String> future = new CompletableFuture<>();
        long time = System.currentTimeMillis();
        scheduled.schedule(() -> future.complete(value), TIME_100.toMillis() + 1000, TimeUnit.MILLISECONDS);

        TypeTaskStage<String> stage = checkStage(
                Stages.yieldForFuture(future, TIME_100)
                , false, null
        );

        assertNotNull(stage.getCause());
        assertTrue(System.currentTimeMillis() >= time + TIME_100.toMillis());
        context.assertIsSatisfied();

    }

    @Test
    public void yieldForWait2() throws Exception {
        long time = System.currentTimeMillis();
        checkStage(
                Stages.yieldForWait(TIME_100)
                , true
        );
        assertTrue(System.currentTimeMillis() >= time + TIME_100.toMillis());
        context.assertIsSatisfied();

    }

    @Test
    public void yieldForWait3() throws Exception {
        long time = System.currentTimeMillis();
        checkStage(
                Stages.yieldForWait(value, TIME_100)
                , true, value
        );
        assertTrue(System.currentTimeMillis() >= time + TIME_100.toMillis());
        context.assertIsSatisfied();

    }
}
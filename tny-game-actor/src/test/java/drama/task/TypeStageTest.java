/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package drama.task;

import com.tny.game.actor.stage.*;
import com.tny.game.actor.stage.invok.*;
import com.tny.game.common.result.*;
import org.jmock.Expectations;
import org.junit.jupiter.api.*;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 有返回值的Stage测试类
 * Created by Kun Yang on 16/2/2.
 */
@SuppressWarnings("unchecked")
class TypeStageTest extends FlowTestUnits {

    // @Test
    // public void testJoinApply() throws Exception {
    //     final Supplier<String> fn = context.mock(Supplier.class);
    //     final Function<String, Stage<String>> tfn = context.mock(Function.class);
    //     context.checking(new Expectations() {{
    //         oneOf(fn).get();
    //         will(returnValue(value));
    //         oneOf(tfn).apply(value);
    //         will(returnValue(Flows.of(() -> other)));
    //     }});
    //     checkFlow(
    //             Flows.of(fn)
    //                     .join(tfn)
    //             , true, other
    //     );
    //     context.assertIsSatisfied();
    // }
    //
    // @Test
    // public void testJoinAccept() throws Exception {
    //     final Runnable runFn = context.mock(Runnable.class);
    //     final Supplier<String> fn = context.mock(Supplier.class);
    //     final Function<String, Stage<String>> tfn = context.mock(Function.class);
    //     context.checking(new Expectations() {{
    //         oneOf(fn).get();
    //         will(returnValue(value));
    //         oneOf(tfn).apply(value);
    //         will(returnValue(Flows.of(runFn)));
    //         oneOf(runFn).run();
    //     }});
    //     checkFlow(
    //             Flows.of(fn)
    //                     .join(tfn)
    //             , true
    //     );
    //     context.assertIsSatisfied();
    // }

    @Test
    public void testThenApply() throws Exception {
        final Supplier<String> fn = this.context.mock(Supplier.class);
        final Function<String, String> tfn = this.context.mock(Function.class);
        this.context.checking(new Expectations() {{
            oneOf(fn).get();
            will(returnValue(value));
            oneOf(tfn).apply(value);
            will(returnValue(other));
        }});
        checkFlow(
                Flows.of(fn)
                        .thenApply(tfn)
                , true, other
        );
        this.context.assertIsSatisfied();
    }

    @Test
    public void testThenAccept() throws Exception {
        final Supplier<String> fn = this.context.mock(Supplier.class);
        final Consumer<String> tfn = this.context.mock(Consumer.class);
        this.context.checking(new Expectations() {{
            oneOf(fn).get();
            will(returnValue(value));
            oneOf(tfn).accept(value);
        }});
        checkFlow(
                Flows.of(fn)
                        .thenAccept(tfn)
                , true
        );
        this.context.assertIsSatisfied();

    }

    @Test
    public void testDoneApply() throws Exception {
        final Supplier<String> fn = this.context.mock(Supplier.class);
        final ApplyDone<String, String> tfn = this.context.mock(ApplyDone.class);

        //正常处理
        this.context.checking(new Expectations() {{
            oneOf(fn).get();
            will(returnValue(value));
            oneOf(tfn).handle(true, value, null);
            will(returnValue(other));
        }});
        checkFlow(
                Flows.of(fn)
                        .doneApply(tfn)
                , true, other
        );
        this.context.assertIsSatisfied();

        //异常回复

        this.context.checking(new Expectations() {{
            oneOf(fn).get();
            will(throwException(exception));
            oneOf(tfn).handle(false, null, exception);
            will(returnValue(value));
        }});
        checkFlow(
                Flows.of(fn).doneApply(tfn)
                , true, value
        );
        this.context.assertIsSatisfied();

        //异常继续抛出
        this.context.checking(new Expectations() {{
            oneOf(fn).get();
            will(throwException(exception));
            oneOf(tfn).handle(false, null, exception);
            will(throwException(exception));
        }});
        checkFlow(
                Flows.of(fn).doneApply(tfn)
                , false
        );
        this.context.assertIsSatisfied();

        //正常 处理抛出
        this.context.checking(new Expectations() {{
            oneOf(fn).get();
            will(returnValue(value));
            oneOf(tfn).handle(true, value, null);
            will(throwException(exception));
        }});
        checkFlow(
                Flows.of(fn)
                        .doneApply(tfn)
                , false
        );
        this.context.assertIsSatisfied();
    }

    @Test
    public void testDoneAccept() throws Exception {
        final Supplier<String> fn = this.context.mock(Supplier.class);
        final AcceptDone<String> tfn = this.context.mock(AcceptDone.class);

        //正常处理
        this.context.checking(new Expectations() {{
            oneOf(fn).get();
            will(returnValue(value));
            oneOf(tfn).handle(true, value, null);
        }});
        checkFlow(
                Flows.of(fn)
                        .doneAccept(tfn)
                , true
        );
        this.context.assertIsSatisfied();

        //异常回复

        this.context.checking(new Expectations() {{
            oneOf(fn).get();
            will(throwException(exception));
            oneOf(tfn).handle(false, null, exception);
        }});
        checkFlow(
                Flows.of(fn).doneAccept(tfn)
                , true
        );
        this.context.assertIsSatisfied();

        //异常继续抛出
        this.context.checking(new Expectations() {{
            oneOf(fn).get();
            will(throwException(exception));
            oneOf(tfn).handle(false, null, exception);
            will(throwException(exception));
        }});
        checkFlow(
                Flows.of(fn).doneAccept(tfn)
                , false
        );
        this.context.assertIsSatisfied();

        //正常 处理抛出
        this.context.checking(new Expectations() {{
            oneOf(fn).get();
            will(returnValue(value));
            oneOf(tfn).handle(true, value, null);
            will(throwException(exception));
        }});
        checkFlow(
                Flows.of(fn)
                        .doneAccept(tfn)
                , false
        );
        this.context.assertIsSatisfied();
    }

    @Test
    public void testThenThrow() throws Exception {
        final Supplier<String> fn = this.context.mock(Supplier.class);
        final CatcherSupplier<String> tfn = this.context.mock(CatcherSupplier.class);

        //正常处理
        this.context.checking(new Expectations() {{
            oneOf(fn).get();
            will(returnValue(value));
            never(tfn).catchThrow(null);
        }});
        checkFlow(
                Flows.of(fn)
                        .thenThrow(tfn)
                , true, value
        );
        this.context.assertIsSatisfied();

        //异常回复
        this.context.checking(new Expectations() {{
            oneOf(fn).get();
            will(throwException(exception));
            oneOf(tfn).catchThrow(exception);
            will(returnValue(other));
        }});
        checkFlow(
                Flows.of(fn)
                        .thenThrow(tfn)
                , true, other
        );
        this.context.assertIsSatisfied();

        //异常继续抛出
        this.context.checking(new Expectations() {{
            oneOf(fn).get();
            will(throwException(exception));
            oneOf(tfn).catchThrow(exception);
            will(throwException(exception));
        }});
        checkFlow(
                Flows.of(fn)
                        .thenThrow(tfn)
                , false
        );
        this.context.assertIsSatisfied();
    }

    @Test
    public void testAwaitApply() throws Exception {
        final Supplier<String> fn = this.context.mock(Supplier.class);
        final Function<String, String> tfn = this.context.mock(Function.class);
        this.context.checking(new Expectations() {{
            oneOf(fn).get();
            will(returnValue(value));
            allowing(tfn).apply(other);
            will(returnValue(other));
        }});
        long time = System.currentTimeMillis();
        AtomicInteger times = new AtomicInteger(0);
        checkFlow(
                Flows.of(fn)
                        .waitFor((value) -> {
                            assertEquals(value, FlowTestUnits.value);
                            times.getAndIncrement();
                            if (System.currentTimeMillis() < time + TIME_100.toMillis()) {
                                return DoneResults.failure();
                            } else {
                                return DoneResults.success(other);
                            }
                        })
                        .thenApply(tfn)
                , true, other
        );
        assertTrue(times.get() > 0);
        assertTrue(System.currentTimeMillis() >= time + TIME_100.toMillis());
        this.context.assertIsSatisfied();
    }

    @Test
    public void testAwaitApply1() throws Exception {
        final Supplier<String> fn = this.context.mock(Supplier.class);
        final Function<String, String> tfn = this.context.mock(Function.class);
        this.context.checking(new Expectations() {{
            oneOf(fn).get();
            will(returnValue(value));
            allowing(tfn).apply(other);
            will(returnValue(other));
        }});
        long time1 = System.currentTimeMillis();
        AtomicInteger times1 = new AtomicInteger(0);
        checkFlow(
                Flows.of(fn)
                        .waitFor((value) -> {
                            assertEquals(value, FlowTestUnits.value);
                            times1.getAndIncrement();
                            if (System.currentTimeMillis() < time1 + TIME_100.toMillis()) {
                                return DoneResults.failure();
                            } else {
                                return DoneResults.success(other);
                            }
                        }, TIME_200)
                        .thenApply(tfn)
                , true, other
        );
        assertTrue(times1.get() > 0);
        assertTrue(System.currentTimeMillis() >= time1 + TIME_100.toMillis());
        this.context.assertIsSatisfied();

        this.context.checking(new Expectations() {{
            oneOf(fn).get();
            will(returnValue(value));
        }});
        long time2 = System.currentTimeMillis();
        AtomicInteger times2 = new AtomicInteger(0);
        checkFlow(
                Flows.of(fn)
                        .waitFor((value) -> {
                            times2.getAndIncrement();
                            if (System.currentTimeMillis() < time2 + TIME_200.toMillis()) {
                                return DoneResults.failure();
                            } else {
                                return DoneResults.success(other);
                            }
                        }, TIME_100)
                        .thenApply(tfn)
                , false
        );
        assertTrue(times2.get() > 0);
        this.context.assertIsSatisfied();

    }

    @Test
    public void testAwaitAccept() throws Exception {
        final Supplier<String> fn = this.context.mock(Supplier.class);
        final Runnable tfn = this.context.mock(Runnable.class);
        this.context.checking(new Expectations() {{
            oneOf(fn).get();
            will(returnValue(value));
            oneOf(tfn).run();
        }});
        long time = System.currentTimeMillis();
        AtomicInteger times = new AtomicInteger(0);
        checkFlow(
                Flows.of(fn)
                        .waitUntil((value) -> {
                            assertEquals(value, FlowTestUnits.value);
                            times.getAndIncrement();
                            return System.currentTimeMillis() >= time + TIME_100.toMillis();
                        })
                        .thenRun(tfn)
                , true
        );
        assertTrue(times.get() > 0);
        assertTrue(System.currentTimeMillis() >= time + TIME_100.toMillis());
        this.context.assertIsSatisfied();

    }

    @Test
    public void testAwaitAccept1() throws Exception {
        final Supplier<String> fn = this.context.mock(Supplier.class);
        final Runnable tfn = this.context.mock(Runnable.class);
        this.context.checking(new Expectations() {{
            oneOf(fn).get();
            will(returnValue(value));
            oneOf(tfn).run();
        }});
        long time1 = System.currentTimeMillis();
        AtomicInteger times1 = new AtomicInteger(0);
        checkFlow(
                Flows.of(fn)
                        .waitUntil((value) -> {
                            assertEquals(value, FlowTestUnits.value);
                            times1.getAndIncrement();
                            return System.currentTimeMillis() >= time1 + TIME_100.toMillis();
                        }, TIME_200)
                        .thenRun(tfn)
                , true
        );
        assertTrue(times1.get() > 0);
        assertTrue(System.currentTimeMillis() >= time1 + TIME_100.toMillis());
        this.context.assertIsSatisfied();

        this.context.checking(new Expectations() {{
            oneOf(fn).get();
            will(returnValue(value));
        }});
        long time2 = System.currentTimeMillis();
        AtomicInteger times2 = new AtomicInteger(0);
        checkFlow(
                Flows.of(fn)
                        .waitUntil((value) -> {
                            times2.getAndIncrement();
                            return System.currentTimeMillis() >= time2 + TIME_200.toMillis();
                        }, TIME_100)
                        .thenRun(tfn)
                , false);
        assertTrue(times2.get() > 0);
        this.context.assertIsSatisfied();
    }

}
/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package drama.task;

import com.tny.game.actor.stage.*;
import com.tny.game.actor.stage.invok.*;
import com.tny.game.common.result.*;
import org.jmock.Expectations;
import org.junit.jupiter.api.*;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 有返回值的Stage测试类
 * Created by Kun Yang on 16/2/2.
 */
@SuppressWarnings("unchecked")
// 类级超时兜底：任何用例若在有界轮询之外仍卡住，三十秒后由 JUnit 判红并释放，形态对齐
// EtcdNamespaceExplorerIT 的类级 @Timeout 兜底（stabilize 判例）。
@Timeout(value = 30, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
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
        // diagnosis.md 案 #1（CI unit 工作流运行号 run#38）定罪本用例族的墙钟窗口判据：测试原先在驱动
        // 循环启动之前抓取 System.currentTimeMillis() 作为重试原点，而生产代码 Stages.WaitFragment 要到
        // stage 首次执行时才抓取超时原点，两个墙钟原点之间隔着调度间隙；CI runner 调度挤压把间隙拉大到
        // 临界值时，重试条件的成功判定与超时判定的先后次序换边，断言随之翻转。
        // 改造把判据换成尝试计数：等待条件第一次调用返回未完成结果、第二次调用返回完成结果，用例末尾断言
        // 条件确实推进到第二次尝试；结论不依赖任何墙钟比较，在任何调度负载下不变。
        AtomicInteger times = new AtomicInteger(0);
        checkFlow(
                Flows.of(fn)
                        .waitFor((value) -> {
                            assertEquals(value, FlowTestUnits.value);
                            if (times.incrementAndGet() < 2) {
                                return DoneResults.failure();
                            } else {
                                return DoneResults.success(other);
                            }
                        })
                        .thenApply(tfn)
                , true, other
        );
        assertTrue(times.get() >= 2);
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
        // diagnosis.md 案 #1（CI unit 工作流运行号 run#38）定罪本用例族的墙钟窗口判据（完整机理见
        // testAwaitApply 处注释）。期望成功段改造为尝试计数判据：等待条件第一次调用返回未完成结果、
        // 第二次调用返回完成结果，用例末尾断言条件确实推进到第二次尝试。
        AtomicInteger times1 = new AtomicInteger(0);
        checkFlow(
                Flows.of(fn)
                        .waitFor((value) -> {
                            assertEquals(value, FlowTestUnits.value);
                            if (times1.incrementAndGet() < 2) {
                                return DoneResults.failure();
                            } else {
                                return DoneResults.success(other);
                            }
                        }, TIME_200)
                        .thenApply(tfn)
                , true, other
        );
        assertTrue(times1.get() >= 2);
        this.context.assertIsSatisfied();

        this.context.checking(new Expectations() {{
            oneOf(fn).get();
            will(returnValue(value));
        }});
        // diagnosis.md 案 #1（CI unit 工作流运行号 run#38）对期望失败段的改造：原先的条件一到墙钟窗口
        // 尽头就返回成功，成功判定与超时判定的先后次序随调度间隙漂移换边，正是负载下断言翻转的来源。
        // 改造把条件改为永不成立，Flow 由 waitFor 传入的超时预算耗尽而失败，失败结论在任何调度负载下不变。
        AtomicInteger times2 = new AtomicInteger(0);
        checkFlow(
                Flows.of(fn)
                        .waitFor((value) -> {
                            times2.getAndIncrement();
                            return DoneResults.<String>failure();
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
        // diagnosis.md 案 #1（CI unit 工作流运行号 run#38）定罪本用例族的墙钟窗口判据（完整机理见
        // testAwaitApply 处注释）。期望成功段改造为尝试计数判据：waitUntil 条件第一次调用返回 false、
        // 第二次调用返回 true，用例末尾断言条件确实推进到第二次尝试。
        AtomicInteger times = new AtomicInteger(0);
        checkFlow(
                Flows.of(fn)
                        .waitUntil((value) -> {
                            assertEquals(value, FlowTestUnits.value);
                            return times.incrementAndGet() >= 2;
                        })
                        .thenRun(tfn)
                , true
        );
        assertTrue(times.get() >= 2);
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
        // diagnosis.md 案 #1（CI unit 工作流运行号 run#38）定罪本用例族的墙钟窗口判据（完整机理见
        // testAwaitApply 处注释）。期望成功段改造为尝试计数判据：waitUntil 条件第一次调用返回 false、
        // 第二次调用返回 true，用例末尾断言条件确实推进到第二次尝试。
        AtomicInteger times1 = new AtomicInteger(0);
        checkFlow(
                Flows.of(fn)
                        .waitUntil((value) -> {
                            assertEquals(value, FlowTestUnits.value);
                            return times1.incrementAndGet() >= 2;
                        }, TIME_200)
                        .thenRun(tfn)
                , true
        );
        assertTrue(times1.get() >= 2);
        this.context.assertIsSatisfied();

        this.context.checking(new Expectations() {{
            oneOf(fn).get();
            will(returnValue(value));
        }});
        // diagnosis.md 案 #1（CI unit 工作流运行号 run#38）对期望失败段的改造：原先的条件一到墙钟窗口
        // 尽头就返回 true，成立判定与超时判定的先后次序随调度间隙漂移换边。改造把条件改为永不成立，
        // Flow 由 waitUntil 传入的超时预算耗尽而失败，失败结论在任何调度负载下不变。
        AtomicInteger times2 = new AtomicInteger(0);
        checkFlow(
                Flows.of(fn)
                        .waitUntil((value) -> {
                            times2.getAndIncrement();
                            return false;
                        }, TIME_100)
                        .thenRun(tfn)
                , false);
        assertTrue(times2.get() > 0);
        this.context.assertIsSatisfied();
    }

}
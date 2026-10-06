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
 * Created by Kun Yang on 16/1/25.
 */
@SuppressWarnings("unchecked")
// 类级超时兜底：任何用例若在有界轮询之外仍卡住，三十秒后由 JUnit 判红并释放，形态对齐
// EtcdNamespaceExplorerIT 的类级 @Timeout 兜底（stabilize 判例）。
@Timeout(value = 30, unit = TimeUnit.SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
public class VoidTypeStageTest extends FlowTestUnits {

    // @Test
    // public void testJoinRun() throws Exception {
    //     final Runnable fn = context.mock(Runnable.class);
    //     context.checking(new Expectations() {{
    //         exactly(3).of(fn).run();
    //     }});
    //     checkFlow(
    //             Flows.of(fn::run)
    //                     .join(() -> Flows.of(fn::run))
    //                     .thenRun(fn::run)
    //             , true
    //     );
    //     context.assertIsSatisfied();
    // }
    //
    // @Test
    // public void testJoinSupply() throws Exception {
    //     final Runnable fn = context.mock(Runnable.class);
    //     final Consumer<String> cfn = context.mock(Consumer.class);
    //     final Supplier<String> tfn = context.mock(Supplier.class);
    //     context.checking(new Expectations() {{
    //         oneOf(fn).run();
    //         oneOf(tfn).get();
    //         will(returnValue(value));
    //         oneOf(cfn).accept(value);
    //         oneOf(fn).run();
    //     }});
    //     checkFlow(
    //             Flows.of(fn::run)
    //                     .join(() -> Flows.of(tfn::get))
    //                     .thenAccept(cfn::accept)
    //                     .thenRun(fn::run)
    //             , true
    //     );
    //     context.assertIsSatisfied();
    // }

    @Test
    public void testThenRun() throws Exception {
        final Runnable fn = context.mock(Runnable.class);
        context.checking(new Expectations() {{
            exactly(2).of(fn).run();
        }});
        checkFlow(
                Flows.of(fn::run)
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
        checkFlow(
                Flows.of(fn::run)
                        .thenGet(tfn::get)
                , true, value
        );
        context.assertIsSatisfied();
    }

    @Test
    public void testDoneRun() throws Exception {
        final Runnable fn = context.mock(Runnable.class);
        final RunDone tfn = context.mock(RunDone.class);
        final Runnable run = () -> {
            throw exception;
        };

        //正常处理
        context.checking(new Expectations() {{
            oneOf(fn).run();
            oneOf(tfn).run(true, null);
        }});
        checkFlow(
                Flows.of(fn::run)
                        .doneRun(tfn)
                , true
        );
        context.assertIsSatisfied();

        //异常回复

        context.checking(new Expectations() {{
            oneOf(tfn).run(false, exception);
        }});
        checkFlow(
                Flows.of(run)
                        .doneRun(tfn)
                , true
        );
        context.assertIsSatisfied();

        //异常继续抛出
        context.checking(new Expectations() {{
            oneOf(tfn).run(false, exception);
            will(throwException(exception));
        }});

        checkFlow(
                Flows.of(run)
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
        checkFlow(
                Flows.of(fn::run)
                        .doneRun(tfn)
                , false
        );
        context.assertIsSatisfied();
    }

    @Test
    public void testDoneSupply() throws Exception {
        final Runnable fn = context.mock(Runnable.class);
        final SupplyDone<String> tfn = context.mock(SupplyDone.class);
        final RunDone rfn = context.mock(RunDone.class);
        final Runnable run = () -> {
            throw exception;
        };

        //正常处理
        context.checking(new Expectations() {{
            oneOf(fn).run();
            oneOf(tfn).handle(true, null);
            will(returnValue(value));
        }});
        checkFlow(
                Flows.of(fn::run)
                        .doneGet(tfn)
                , true, value
        );
        context.assertIsSatisfied();

        //异常回复

        context.checking(new Expectations() {{
            oneOf(tfn).handle(false, exception);
            will(returnValue(value));
        }});
        checkFlow(
                Flows.of(run)
                        .doneGet(tfn)
                , true, value
        );
        context.assertIsSatisfied();

        //异常继续抛出
        context.checking(new Expectations() {{
            oneOf(rfn).run(false, exception);
            will(throwException(exception));
        }});
        checkFlow(
                Flows.of(run)
                        .doneRun(rfn)
                , false
        );
        context.assertIsSatisfied();

        //正常 处理抛出
        context.checking(new Expectations() {{
            oneOf(fn).run();
            oneOf(tfn).handle(true, null);
            will(throwException(exception));
        }});
        checkFlow(
                Flows.of(fn::run)
                        .doneGet(tfn)
                , false, null
        );
        context.assertIsSatisfied();
    }

    @Test
    public void testThenThrow() throws Exception {
        final Runnable fn = context.mock(Runnable.class);
        final CatcherRun tfn = context.mock(CatcherRun.class);
        final Runnable run = () -> {
            throw exception;
        };

        //正常处理
        context.checking(new Expectations() {{
            oneOf(fn).run();
            never(tfn).catchThrow(null);
        }});
        checkFlow(
                Flows.of(fn::run)
                        .thenThrow(tfn)
                , true
        );
        context.assertIsSatisfied();

        //异常回复

        context.checking(new Expectations() {{
            oneOf(tfn).catchThrow(exception);
        }});
        checkFlow(
                Flows.of(run)
                        .thenThrow(tfn)
                , true
        );
        context.assertIsSatisfied();

        //异常继续抛出
        context.checking(new Expectations() {{
            oneOf(tfn).catchThrow(exception);
            will(throwException(exception));
        }});
        checkFlow(
                Flows.of(run)
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
        // diagnosis.md 案 #1（CI unit 工作流运行号 run#38）的处方覆盖整个测试族，diagnosis.md 本地登记 #1
        // （2026-10-02 全量并行构建下 VoidTypeStageTest.testAwaitRun1 一次偶红）是同族相邻案件。本用例原先
        // 用生产助手 Flows.time 的墙钟窗口作等待条件，并在用例末尾把驱动循环启动前抓取的墙钟原点与该窗口
        // 的到期时刻比较；而生产代码 Stages.WaitFragment 要到 stage 首次执行时才抓取超时原点，两个墙钟原点
        // 之间隔着调度间隙，CI runner 调度挤压把间隙拉大到临界值时断言就可能翻转。改造把判据换成尝试计数：
        // 等待条件第一次调用返回 false、第二次调用返回 true，用例末尾断言条件确实推进到第二次尝试。
        AtomicInteger times = new AtomicInteger(0);
        checkFlow(
                Flows.of(fn::run)
                        .waitUntil(() -> times.incrementAndGet() >= 2)
                        .thenRun(fn::run)
                , true
        );
        assertTrue(times.get() >= 2);
        context.assertIsSatisfied();
    }

    @Test
    public void testAwaitRun1() throws Exception {
        final Runnable fn = context.mock(Runnable.class);
        context.checking(new Expectations() {{
            exactly(2).of(fn).run();
        }});
        // diagnosis.md 案 #1（CI unit 工作流运行号 run#38）的处方覆盖整个测试族，diagnosis.md 本地登记 #1
        // （2026-10-02 全量并行构建下 VoidTypeStageTest.testAwaitRun1 一次偶红）正是本案卷点名的用例。
        // 墙钟窗口判据的完整机理见 testAwaitRun 处注释。期望成功段改造为尝试计数判据：等待条件第一次调用
        // 返回 false、第二次调用返回 true，用例末尾断言条件确实推进到第二次尝试。
        AtomicInteger times1 = new AtomicInteger(0);
        checkFlow(
                Flows.of(fn::run)
                        .waitUntil(() -> times1.incrementAndGet() >= 2, TIME_200)
                        .thenRun(fn::run)
                , true
        );
        assertTrue(times1.get() >= 2);
        context.assertIsSatisfied();

        context.checking(new Expectations() {{
            exactly(1).of(fn).run();
        }});
        // diagnosis.md 案 #1（CI unit 工作流运行号 run#38）对期望失败段的改造：原先的墙钟条件到窗口尽头
        // 就会成立，成立判定与超时判定的先后次序随调度间隙漂移换边。改造把条件改为永不成立，Flow 由
        // waitUntil 传入的超时预算耗尽而失败，失败结论在任何调度负载下不变；计数自增保留重试确实发生的验证。
        AtomicInteger times2 = new AtomicInteger(0);
        checkFlow(
                Flows.of(fn::run)
                        .waitUntil(() -> {
                            times2.getAndIncrement();
                            return false;
                        }, TIME_100)
                        .thenRun(fn::run)
                , false
        );
        assertTrue(times2.get() > 0);
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
        // diagnosis.md 案 #1（CI unit 工作流运行号 run#38）的处方覆盖整个测试族，diagnosis.md 本地登记 #1
        // （2026-10-02 全量并行构建下 VoidTypeStageTest.testAwaitRun1 一次偶红）是同族相邻案件。墙钟窗口
        // 判据的完整机理见 testAwaitRun 处注释。期望成功段改造为尝试计数判据：等待条件第一次调用返回
        // 未完成结果、第二次调用返回完成结果，用例末尾断言条件确实推进到第二次尝试。
        AtomicInteger times = new AtomicInteger(0);
        checkFlow(
                Flows.of(fn::run)
                        .waitFor(() -> {
                            if (times.incrementAndGet() < 2) {
                                return DoneResults.failure();
                            } else {
                                return DoneResults.success(value);
                            }
                        })
                        .thenApply(cfn)
                , true, value
        );
        assertTrue(times.get() >= 2);
        context.assertIsSatisfied();

    }

    @Test
    public void testAwaitSupply1() throws Exception {

        final Runnable fn = context.mock(Runnable.class);
        final Consumer<String> cfn = context.mock(Consumer.class);
        final Function<String, String> ffn = context.mock(Function.class);
        context.checking(new Expectations() {{
            exactly(1).of(fn).run();
            oneOf(ffn).apply(value);
            will(returnValue(value));
        }});
        // diagnosis.md 案 #1（CI unit 工作流运行号 run#38）的处方覆盖整个测试族，diagnosis.md 本地登记 #1
        // （2026-10-02 全量并行构建下 VoidTypeStageTest.testAwaitRun1 一次偶红）是同族相邻案件。墙钟窗口
        // 判据的完整机理见 testAwaitRun 处注释。期望成功段改造为尝试计数判据：等待条件第一次调用返回
        // 未完成结果、第二次调用返回完成结果，用例末尾断言条件确实推进到第二次尝试。
        AtomicInteger times1 = new AtomicInteger(0);
        checkFlow(
                Flows.of(fn::run)
                        .waitFor(() -> {
                            if (times1.incrementAndGet() < 2) {
                                return DoneResults.failure();
                            } else {
                                return DoneResults.success(value);
                            }
                        }, TIME_200)
                        .thenApply(ffn)
                , true, value
        );
        assertTrue(times1.get() >= 2);
        context.assertIsSatisfied();

        context.checking(new Expectations() {{
            exactly(1).of(fn).run();
            never(cfn).accept(value);
        }});
        // diagnosis.md 案 #1（CI unit 工作流运行号 run#38）对期望失败段的改造：原先的墙钟条件到窗口尽头
        // 就会返回完成结果，成立判定与超时判定的先后次序随调度间隙漂移换边。改造把条件改为永不成立，
        // Flow 由 waitFor 传入的超时预算耗尽而失败，失败结论在任何调度负载下不变；计数自增保留重试
        // 确实发生的验证。
        AtomicInteger times2 = new AtomicInteger(0);
        checkFlow(
                Flows.of(fn::run)
                        .waitFor(() -> {
                            times2.getAndIncrement();
                            return DoneResults.<String>failure();
                        }, TIME_100)
                        .thenAccept(cfn::accept)
                , false
        );
        assertTrue(times2.get() > 0);
        context.assertIsSatisfied();
    }

}
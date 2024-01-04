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

package drama.task;

import com.tny.game.actor.stage.*;
import com.tny.game.actor.stage.invok.*;
import org.jmock.Expectations;
import org.junit.jupiter.api.*;

import java.util.function.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Kun Yang on 16/1/25.
 */
@SuppressWarnings("unchecked")
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
        long time = System.currentTimeMillis();
        checkFlow(
                Flows.of(fn::run)
                        .waitUntil(Flows.time(TIME_100))
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
        checkFlow(
                Flows.of(fn::run)
                        .waitUntil(Flows.time(TIME_100), TIME_200)
                        .thenRun(fn::run)
                , true
        );
        assertTrue(System.currentTimeMillis() >= time + TIME_100.toMillis());
        context.assertIsSatisfied();

        context.checking(new Expectations() {{
            exactly(1).of(fn).run();
        }});
        checkFlow(
                Flows.of(fn::run)
                        .waitUntil(Flows.time(TIME_200), TIME_100)
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
        checkFlow(
                Flows.of(fn::run)
                        .waitFor(Flows.time(value, TIME_100))
                        .thenApply(cfn)
                , true, value
        );
        assertTrue(System.currentTimeMillis() >= time + TIME_100.toMillis());
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
        long time = System.currentTimeMillis();
        checkFlow(
                Flows.of(fn::run)
                        .waitFor(Flows.time(value, TIME_100), TIME_200)
                        .thenApply(ffn)
                , true, value
        );
        assertTrue(System.currentTimeMillis() >= time + TIME_100.toMillis());
        context.assertIsSatisfied();

        context.checking(new Expectations() {{
            exactly(1).of(fn).run();
            never(cfn).accept(value);
        }});
        checkFlow(
                Flows.of(fn::run)
                        .waitFor(Flows.time(value, TIME_200), TIME_100)
                        .thenAccept(cfn::accept)
                , false
        );
        context.assertIsSatisfied();
    }

}
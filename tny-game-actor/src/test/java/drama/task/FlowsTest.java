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

import com.tny.game.actor.*;
import com.tny.game.actor.stage.*;
import com.tny.game.common.result.*;
import org.jmock.Expectations;
import org.junit.jupiter.api.*;

import java.time.Duration;
import java.util.concurrent.*;
import java.util.function.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Kun Yang on 16/1/25.
 */
@SuppressWarnings("unchecked")
public class FlowsTest extends FlowTestUnits {

    @Test
    public void testRun() throws Exception {
        final Runnable fn = this.context.mock(Runnable.class);
        this.context.checking(new Expectations() {{
            oneOf(fn).run();
        }});
        checkFlow(
                Flows.of(fn::run)
                , true
        );
        this.context.assertIsSatisfied();
    }

    @Test
    public void testSupply() throws Exception {
        final Supplier<String> fn = this.context.mock(Supplier.class);

        this.context.checking(new Expectations() {{
            oneOf(fn).get();
            will(returnValue(value));
        }});
        checkFlow(
                Flows.of(fn::get)
                , true, value
        );
        this.context.assertIsSatisfied();
    }

    @Test
    public void testAwaitRun() throws Exception {
        final BooleanSupplier fn = this.context.mock(BooleanSupplier.class);
        this.context.checking(new Expectations() {{
            exactly(3).of(fn).getAsBoolean();
            will(onConsecutiveCalls(
                    returnValue(false),
                    returnValue(false),
                    returnValue(true)
            ));
        }});
        checkFlow(
                Flows.waitUntil(fn::getAsBoolean)
                , true
        );
        this.context.assertIsSatisfied();
    }

    @Test
    public void testAwaitRun1() throws Exception {
        VoidFlow flow = checkFlow(
                Flows.waitUntil(() -> false, Duration.ofMillis(10))
                , false
        );
        assertNotNull(flow.getCause());
        this.context.assertIsSatisfied();
    }

    @Test
    public void testAwaitSupply() throws Exception {
        final Supplier<Done<String>> fn = this.context.mock(Supplier.class);
        this.context.checking(new Expectations() {{
            exactly(3).of(fn).get();
            will(onConsecutiveCalls(
                    returnValue(DoneResults.failure()),
                    returnValue(DoneResults.failure()),
                    returnValue(DoneResults.success(value))
            ));
        }});
        checkFlow(
                Flows.waitFor(fn::get)
                , true, value
        );
        this.context.assertIsSatisfied();

    }

    @Test
    public void testAwaitSupply1() throws Exception {
        DoneSupplier<String> fn = DoneResults::failure;
        TypeFlow<String> flow = checkFlow(
                Flows.waitFor(fn, TIME_100)
                , false, null
        );
        assertNotNull(flow.getCause());
        this.context.assertIsSatisfied();
    }

    @Test
    public void yieldForFuture() throws Exception {
        final CompletableFuture<String> future = new CompletableFuture<>();
        long time = System.currentTimeMillis();
        this.scheduled.schedule(() -> future.complete(value), TIME_100.toMillis(), TimeUnit.MILLISECONDS);

        checkFlow(
                Flows.waitFor(future)
                , true, value
        );

        assertTrue(System.currentTimeMillis() >= time + TIME_100.toMillis());
        this.context.assertIsSatisfied();

    }

    @Test
    public void yieldForFuture1() throws Exception {
        final CompletableFuture<String> future = new CompletableFuture<>();
        long time = System.currentTimeMillis();
        this.scheduled.schedule(() -> future.complete(value), TIME_100.toMillis() + 1000, TimeUnit.MILLISECONDS);

        TypeFlow<String> flow = checkFlow(
                Flows.waitFor(future, TIME_100)
                , false, null
        );

        assertNotNull(flow.getCause());
        assertTrue(System.currentTimeMillis() >= time + TIME_100.toMillis());
        this.context.assertIsSatisfied();

    }

    @Test
    public void yieldForWait2() throws Exception {
        long time = System.currentTimeMillis();
        checkFlow(
                Flows.waitTime(TIME_100)
                , true
        );
        assertTrue(System.currentTimeMillis() >= time + TIME_100.toMillis());
        this.context.assertIsSatisfied();

    }

    @Test
    public void yieldForWait3() throws Exception {
        long time = System.currentTimeMillis();
        checkFlow(
                Flows.waitTime(value, TIME_100)
                , true, value
        );
        assertTrue(System.currentTimeMillis() >= time + TIME_100.toMillis());
        this.context.assertIsSatisfied();

    }

}
package drama.task;

import com.tny.game.actor.*;
import com.tny.game.actor.stage.*;
import com.tny.game.common.utils.*;
import org.jmock.Expectations;
import org.junit.*;

import java.time.Duration;
import java.util.concurrent.*;
import java.util.function.*;

import static org.junit.Assert.*;


/**
 * Created by Kun Yang on 16/1/25.
 */
@SuppressWarnings("unchecked")
public class FlowsTest extends FlowTestUnits {

    @Test
    public void testRun() throws Exception {
        final Runnable fn = context.mock(Runnable.class);
        context.checking(new Expectations() {{
            oneOf(fn).run();
        }});
        checkFlow(
                Flows.of(fn::run)
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
        checkFlow(
                Flows.of(fn::get)
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
        checkFlow(
                Flows.waitUntil(fn::getAsBoolean)
                , true
        );
        context.assertIsSatisfied();
    }

    @Test
    public void testAwaitRun1() throws Exception {
        VoidFlow flow = checkFlow(
                Flows.waitUntil(() -> false, Duration.ofMillis(10))
                , false
        );
        assertNotNull(flow.getCause());
        context.assertIsSatisfied();
    }

    @Test
    public void testAwaitSupply() throws Exception {
        final Supplier<Done<String>> fn = context.mock(Supplier.class);
        context.checking(new Expectations() {{
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
        context.assertIsSatisfied();

    }

    @Test
    public void testAwaitSupply1() throws Exception {
        DoneSupplier<String> fn = DoneResults::failure;
        TypeFlow<String> flow = checkFlow(
                Flows.waitFor(fn, TIME_100)
                , false, null
        );
        assertNotNull(flow.getCause());
        context.assertIsSatisfied();
    }

    @Test
    public void yieldForFuture() throws Exception {
        final CompletableFuture<String> future = new CompletableFuture<>();
        long time = System.currentTimeMillis();
        scheduled.schedule(() -> future.complete(value), TIME_100.toMillis(), TimeUnit.MILLISECONDS);

        checkFlow(
                Flows.waitFor(future)
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

        TypeFlow<String> flow = checkFlow(
                Flows.waitFor(future, TIME_100)
                , false, null
        );

        assertNotNull(flow.getCause());
        assertTrue(System.currentTimeMillis() >= time + TIME_100.toMillis());
        context.assertIsSatisfied();

    }

    @Test
    public void yieldForWait2() throws Exception {
        long time = System.currentTimeMillis();
        checkFlow(
                Flows.waitTime(TIME_100)
                , true
        );
        assertTrue(System.currentTimeMillis() >= time + TIME_100.toMillis());
        context.assertIsSatisfied();

    }

    @Test
    public void yieldForWait3() throws Exception {
        long time = System.currentTimeMillis();
        checkFlow(
                Flows.waitTime(value, TIME_100)
                , true, value
        );
        assertTrue(System.currentTimeMillis() >= time + TIME_100.toMillis());
        context.assertIsSatisfied();

    }
}
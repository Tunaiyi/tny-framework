package drama.task;


import com.tny.game.actor.stage.Flow;
import com.tny.game.actor.stage.TypeFlow;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static org.junit.Assert.*;

/**
 * Created by Kun Yang on 16/1/25.
 */
public class FlowTestUnits {

    Mockery context = new JUnit4Mockery();

    ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(1);

    static final String value = "10000";
    static final String other = "20000";
    static final String other_value = "2000010000";
    static final RuntimeException exception = new RuntimeException();

    static final Duration TIME_100 = Duration.ofMillis(100);

    static final Duration TIME_200 = Duration.ofMillis(200);

    public <TS extends Flow> TS checkFlow(TS flow, boolean success) {
        while (!flow.isDone()) {
            flow.run();
        }
        boolean result = flow.isSuccess();
        if (!result && success) {
            System.out.println("TaskStage异常");
            flow.getCause().printStackTrace();
            assertEquals(exception, flow.getCause());
        }
        assertEquals("TaskStage 结果 : ", success, result);
        return flow;
    }

    public <T, TS extends TypeFlow<T>> TS checkFlow(TS flow, boolean success, T object) {
        while (!flow.isDone()) {
            flow.run();
        }
        boolean result = flow.isSuccess();
        if (!result && success) {
            System.out.println("TaskStage异常");
            flow.getCause().printStackTrace();
        }
        assertEquals("TaskStage 结果 : ", success, result);
        if (object == null)
            assertNull(flow.getDone().get());
        else
            assertEquals("TaskStage 结果 : ", object, flow.getDone().get());
        return flow;
    }

}

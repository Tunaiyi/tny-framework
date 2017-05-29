package drama.task;


import com.tny.game.actor.stage.Stage;
import com.tny.game.actor.stage.Stages;
import com.tny.game.actor.stage.TypeStage;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static org.junit.Assert.*;

/**
 * Created by Kun Yang on 16/1/25.
 */
public class TaskStageTestUnits {

    Mockery context = new JUnit4Mockery();

    ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(1);

    static final String value = "10000";
    static final String other = "20000";
    static final String other_value = "2000010000";
    static final RuntimeException exception = new RuntimeException();

    static final Duration TIME_100 = Duration.ofMillis(100);

    static final Duration TIME_200 = Duration.ofMillis(200);

    public <TS extends Stage> TS checkStage(TS stage, boolean success) {
        while (!stage.isDone()) {
            Stages.process(stage);
        }
        boolean result = stage.isSuccess();
        if (!result && success) {
            System.out.println("TaskStage异常");
            stage.getCause().printStackTrace();
            assertEquals(exception, stage.getCause());
        }
        assertEquals("TaskStage 结果 : ", success, result);
        return stage;
    }

    public <T, TS extends TypeStage<T>> TS checkStage(TS stage, boolean success, T object) {
        while (!stage.isDone()) {
            Stages.process(stage);
        }
        boolean result = stage.isSuccess();
        if (!result && success) {
            System.out.println("TaskStage异常");
            stage.getCause().printStackTrace();
        }
        assertEquals("TaskStage 结果 : ", success, result);
        if (object == null)
            assertNull(stage.getResult());
        else
            assertEquals("TaskStage 结果 : ", object, stage.getResult());
        return stage;
    }

}

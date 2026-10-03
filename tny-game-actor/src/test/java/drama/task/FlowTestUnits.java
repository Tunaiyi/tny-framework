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

import com.tny.game.actor.stage.Flow;
import com.tny.game.actor.stage.*;
import org.jmock.Mockery;
import org.jmock.junit5.JUnit5Mockery;

import java.time.Duration;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Kun Yang on 16/1/25.
 */
public class FlowTestUnits {

    Mockery context = new JUnit5Mockery();

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
        assertEquals(success, result, "TaskStage 结果 : ");
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
        assertEquals(success, result, "TaskStage 结果 : ");
        if (object == null) {
            assertNull(flow.getDone().get());
        } else {
            assertEquals(object, flow.getDone().get(), "TaskStage 结果 : ");
        }
        return flow;
    }

}

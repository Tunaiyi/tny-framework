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

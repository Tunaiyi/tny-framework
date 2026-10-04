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
import org.jmock.lib.concurrent.Synchroniser;

import java.time.Duration;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Kun Yang on 16/1/25.
 */
public class FlowTestUnits {

    // TypeStageTest 与 VoidTypeStageTest 带类级 @Timeout(threadMode = SEPARATE_THREAD)，JUnit 把它们的
    // 整个测试方法调度到独立线程执行，而本字段在主线程构造测试实例时初始化（同包未带该注解的子类测试
    // 仍在调用线程执行，Synchroniser 对单线程使用无害）。jmock 默认的 SingleThreadedPolicy 把 Mockery 构造时所在的
    // 线程固化为唯一合法调用线程，独立线程上的第一次 mock 操作就会抛出 ConcurrentModificationException
    // （异常信息本身提示用 Synchroniser 保证线程安全）。这里按 jmock 官方做法改用 Synchroniser 线程策略：
    // mock 调用改为同步排队而非线程身份检查，测试方法内部仍由单一独立线程顺序驱动，断言语义不变。
    Mockery context = createThreadSafeMockery();

    private static JUnit5Mockery createThreadSafeMockery() {
        JUnit5Mockery mockery = new JUnit5Mockery();
        mockery.setThreadingPolicy(new Synchroniser());
        return mockery;
    }

    ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(1);

    static final String value = "10000";

    static final String other = "20000";

    static final String other_value = "2000010000";

    static final RuntimeException exception = new RuntimeException();

    static final Duration TIME_100 = Duration.ofMillis(100);

    static final Duration TIME_200 = Duration.ofMillis(200);

    // 驱动 Flow 走向完成的轮询循环加上十秒绝对上限窗口（diagnosis.md 案 #1（CI unit 工作流运行号 run#38）
    // 的同族根治）：原先是无上限紧循环，Flow 若始终不完成就永久挂死且不报出失败信息。该窗口只用作看门狗
    // 对驱动循环自身快速判红，不改变循环体的推进语义，也不参与 Flow 成功与否的结果判定。
    static final Duration TIME_10000 = Duration.ofSeconds(10);

    public <TS extends Flow> TS checkFlow(TS flow, boolean success) {
        long deadline = System.currentTimeMillis() + TIME_10000.toMillis();
        while (!flow.isDone()) {
            if (System.currentTimeMillis() > deadline) {
                fail("Flow 未在有界轮询窗口内完成。驱动轮询已持续超过 " + TIME_10000.toMillis()
                        + " 毫秒，flow.isDone() 仍返回 false。");
            }
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
        // 与上一个重载同样加上十秒绝对上限窗口的看门狗，说明见 TIME_10000 常量处的注释。
        long deadline = System.currentTimeMillis() + TIME_10000.toMillis();
        while (!flow.isDone()) {
            if (System.currentTimeMillis() > deadline) {
                fail("Flow 未在有界轮询窗口内完成。驱动轮询已持续超过 " + TIME_10000.toMillis()
                        + " 毫秒，flow.isDone() 仍返回 false。");
            }
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

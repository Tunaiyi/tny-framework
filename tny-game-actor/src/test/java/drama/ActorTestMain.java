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

package drama;

import com.tny.game.actor.*;
import com.tny.game.actor.local.*;
import com.tny.game.actor.stage.*;

import java.util.concurrent.*;

/**
 * Created by Kun Yang on 16/4/30.
 */
public class ActorTestMain {

    static class TestService {

        public void tell(Actor<String, Object> actor) {
            System.out.println("tell " + actor.getActorId());
        }

        public String askName(Actor<String, Object> actor) {
            System.out.println("askName " + actor.getActorId());
            return actor.getActorId();
        }

        public String askAge(Actor<String, Object> actor, String name) {
            System.out.println("askAge " + actor.getActorId());
            return name + " 是 " + 10 + "岁";
        }

    }

    private static LocalActorContext<String, Object> context = new LocalActorContext<>(null);

    public static void main(String[] args) throws InterruptedException {
        TestService service = new TestService();
        ActorProps.of(ActorProps.of().setActorHandler(mail -> {
            Object message = mail.getMessage();
            if (message instanceof String) {

            }
            return null;
        }));
        LocalActor<String, Object> actor1 = context.actorOf("Actor1");
        LocalActor<String, Object> actor2 = context.actorOf("Actor2");

        VoidFlow flow = Flows.of(actor1)
                .thenRun(() -> service.tell(actor1))
                .switchTo(actor2)
                .thenGet(() -> service.askName(actor2))
                .thenApply((name) -> service.askAge(actor2, name))
                .thenAccept((message) -> {
                    service.tell(actor2);
                    System.out.println(message);
                })
                .start();

        while (!flow.isDone()) {
            Thread.sleep(10);
        }
        // System.out.println(StageUtils.getResult(answer.stage()).get());

        long time = System.currentTimeMillis() + 5000;

        Future<String> future = new CompletableFuture<>();
        flow = Flows.of(actor1)
                .thenRun(() -> System.out.println("finish tell until"))
                .start();

        while (!flow.isDone()) {
            Thread.sleep(10);
        }
        context.stopAll();
    }

}

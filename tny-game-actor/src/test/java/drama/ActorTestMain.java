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

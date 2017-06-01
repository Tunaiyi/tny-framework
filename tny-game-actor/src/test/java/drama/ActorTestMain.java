package drama;


import com.tny.game.actor.Actor;
import com.tny.game.actor.local.LocalActor;
import com.tny.game.actor.local.LocalActorContext;
import com.tny.game.actor.stage.Flows;
import com.tny.game.actor.stage.VoidFlow;

/**
 * Created by Kun Yang on 16/4/30.
 */
public class ActorTestMain {

    static class TestService {

        public void tell(Actor<String, Object> actor) {
            System.out.println("tell " + actor.getActorID());
        }

        public String askName(Actor<String, Object> actor) {
            System.out.println("askName " + actor.getActorID());
            return actor.getActorID();
        }

        public String askAge(Actor<String, Object> actor, String name) {
            System.out.println("askAge " + actor.getActorID());
            return name + " 是 " + 10 + "岁";
        }

    }


    private static LocalActorContext<String, Object> context = new LocalActorContext<>(null);

    public static void main(String[] args) throws InterruptedException {
        TestService service = new TestService();
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

        flow = Flows.of(actor1)
                .waitUntil(() -> System.currentTimeMillis() > time)
                .thenRun(() -> System.out.println("finish tell until"))
                .start();

        while (!flow.isDone()) {
            Thread.sleep(10);
        }
        context.stopAll();
    }

}

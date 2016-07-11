package drama;


import com.tny.game.actor.Actor;
import com.tny.game.actor.VoidAnswer;
import com.tny.game.actor.local.LocalActor;
import com.tny.game.actor.local.LocalActorContext;

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


        System.out.println(Thread.currentThread());
        VoidAnswer answer = actor1.asTeller(service::tell)
                .then((stage) -> stage
                        .joinGet(() -> actor2.asAsker(service::askName).ask())
                        .joinApply((name) -> actor2.asAsker(service::askAge).ask(name))
                        .thenAccept((message) -> {
                            actor2.asTeller(service::tell).tell();
                            System.out.println(message);
                        }))
                .tellOf();

        while (!answer.isDone()) {
            Thread.sleep(10);
        }
        // System.out.println(StageUtils.getResult(answer.stage()).get());

        long time = System.currentTimeMillis() + 5000;

        answer = actor1.tellUntil(() -> System.currentTimeMillis() > time)
                .then(
                        stage -> stage.thenRun(() -> System.out.println("finish tell until"))
                )
                .tellOf();

        while (!answer.isDone()) {
            Thread.sleep(10);
        }

    }

}

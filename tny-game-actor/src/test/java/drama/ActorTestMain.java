package drama;


import com.tny.game.actor.Actor;
import com.tny.game.actor.VoidAnswer;
import com.tny.game.actor.local.LocalActor;
import com.tny.game.actor.local.LocalActorContext;
import com.tny.game.actor.stage.StageUtils;

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
        VoidAnswer answer =
                actor1.tellToAccept(service::tell)
                        .stage((stage) -> stage
                                .joinBy(() -> actor2.askToAnswer(service::askName).ask())
                                .joinBy((name) -> actor2.askToAnswer(service::askAge).ask(name))
                                .thenAccept((message) -> {
                                    actor2.tellToAccept(service::tell).tell();
                                    System.out.println(message);
//                                    return message;
                                }))
                        .tellOf();

        while (!answer.isDone()) {
            Thread.sleep(10);
        }
        System.out.println(StageUtils.getResult(answer.stage()).get());

        long time = System.currentTimeMillis() + 5000;

        answer = actor1.tellUntil(() -> System.currentTimeMillis() > time)
                .stage(
                        stage -> stage.thenRun(() -> System.out.println("finish tell until"))
                )
                .tellOf();

        while (!answer.isDone()) {
            Thread.sleep(10);
        }

    }

}
package drama.actor;


public interface InnerActorRef extends ActorRef {

    boolean isTerminated();

    void start();

    void resume(Throwable caused);

    void suspend();

    void restart(Throwable caused);

    void stop();

    void sendSystemMessage(SystemMessage message);

    ActorContext<?> getContext();

}

package drama.stage.invok;

/**
 * Created by Kun Yang on 16/1/23.
 */
@FunctionalInterface
public interface RunDone {

    void run(boolean success, Throwable cause);

}

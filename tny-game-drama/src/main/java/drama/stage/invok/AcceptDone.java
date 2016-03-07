package drama.stage.invok;

/**
 * Created by Kun Yang on 16/1/23.
 */
@FunctionalInterface
public interface AcceptDone<T> {

    void handle(boolean success, T value, Throwable cause);

}

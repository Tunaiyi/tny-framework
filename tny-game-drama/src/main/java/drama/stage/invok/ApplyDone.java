package drama.stage.invok;

/**
 * Created by Kun Yang on 16/1/23.
 */
@FunctionalInterface
public interface ApplyDone<V, R> {

    R handle(boolean success, V value, Throwable cause);

}
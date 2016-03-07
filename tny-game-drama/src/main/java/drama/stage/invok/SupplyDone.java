package drama.stage.invok;

/**
 * Created by Kun Yang on 16/1/23.
 */
@FunctionalInterface
public interface SupplyDone<R> {

    R handle(boolean success, Throwable cause);

}
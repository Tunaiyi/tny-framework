package drama.stage.invok;

/**
 * Created by Kun Yang on 16/1/23.
 */
public interface CatcherSupplier<R> {

    R catchThrow(Throwable cause);

}

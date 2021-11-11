import com.tny.game.net.command.*;

import java.util.concurrent.Future;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/2 5:28 下午
 */
public interface TInterface {

	Future<RpcResult<String>> future();

	String string();

}

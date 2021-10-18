package com.tny.game.data.storage;

import com.tny.game.data.accessor.*;
import com.tny.game.data.cache.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/28 12:59 下午
 */
public interface StorageAccessorFactory {

	<A extends StorageAccessor<?, ?>> A createAccessor(EntityScheme scheme, EntityKeyMaker<?, ?> keyMaker);

}

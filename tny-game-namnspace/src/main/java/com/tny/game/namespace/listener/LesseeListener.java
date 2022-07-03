package com.tny.game.namespace.listener;

import com.tny.game.namespace.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/6/29 03:16
 **/
public interface LesseeListener {

    void onRenew(Lessee source);

    void onError(Lessee source, Throwable cause);

    void onCompleted(Lessee source);

}

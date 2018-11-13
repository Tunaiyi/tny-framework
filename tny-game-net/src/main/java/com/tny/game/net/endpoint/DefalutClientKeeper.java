package com.tny.game.net.endpoint;

import com.tny.game.net.exception.ValidatorFailException;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-11-07 16:55
 */
public class DefalutClientKeeper<UID> extends AbstractEndpointKeeper<UID, Client<UID>, Client<UID>> implements ClientKeeper<UID> {

    protected DefalutClientKeeper(String userType) {
        super(userType);
    }

    @Override
    public void register(Client<UID> client) {
        this.endpointMap.put(client.getUserId(), client);
    }

    @Override
    public void unregister(Client<UID> client) throws ValidatorFailException {
        this.endpointMap.remove(client.getUserId(), client);
    }

}

package com.tny.game.net.transport;

import com.tny.game.common.context.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-09-18 11:50
 */
public abstract class AbstractCommunicator<UID> extends AttributesHolder implements Communicator<UID> {

    protected AbstractCommunicator() {
    }

    protected void destroyFutureHolder() {
        RespondFutureHolder.removeHolder(this);
    }

    @Override
    public UID getUserId() {
        return getCertificate().getUserId();
    }

    @Override
    public String getUserType() {
        return getCertificate().getUserType();
    }

    @Override
    public boolean isLogin() {
        return this.getCertificate().isAuthenticated();
    }

}

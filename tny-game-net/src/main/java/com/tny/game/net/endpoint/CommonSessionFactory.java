package com.tny.game.net.endpoint;

import com.tny.game.net.command.*;

/**
 * <p>
 */
public class CommonSessionFactory<UID> implements SessionFactory<UID, CommonSession<UID>, SessionSetting> {

    public CommonSessionFactory() {
    }

    @Override
    public CommonSession<UID> create(SessionSetting setting, EndpointContext endpointContext, CertificateFactory<UID> certificateFactory) {
        return new CommonSession<>(setting, certificateFactory.anonymous(), endpointContext);
    }

}

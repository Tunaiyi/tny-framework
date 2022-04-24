package com.tny.game.net.endpoint;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.command.*;

/**
 * <p>
 */
@UnitInterface
public interface EndpointFactory<UID, E extends Endpoint<UID>, S extends EndpointSetting> {

    E create(S setting, EndpointContext endpointContext, CertificateFactory<UID> certificateFactory);

}

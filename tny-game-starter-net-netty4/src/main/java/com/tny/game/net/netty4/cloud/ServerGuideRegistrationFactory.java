package com.tny.game.net.netty4.cloud;

import com.tny.game.net.base.*;
import org.springframework.cloud.client.serviceregistry.Registration;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/9 3:03 下午
 */
public interface ServerGuideRegistrationFactory {

    Registration create(ServerGuide guide, NetAppContext appContext);

}

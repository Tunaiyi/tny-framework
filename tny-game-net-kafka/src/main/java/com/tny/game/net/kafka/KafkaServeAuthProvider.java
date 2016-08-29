package com.tny.game.net.kafka;

import com.tny.game.net.LoginCertificate;
import com.tny.game.net.dispatcher.AuthProvider;
import com.tny.game.net.dispatcher.Request;
import com.tny.game.net.dispatcher.exception.DispatchException;

/**
 * Created by Kun Yang on 16/8/10.
 */
public interface KafkaServeAuthProvider extends AuthProvider {

    @Override
    default boolean isCanValidate(Request request) {
        return request instanceof KafkaRequest;
    }

    @Override
    default LoginCertificate validate(Request request) throws DispatchException {
        LoginCertificate certificate = request.getSession().attributes().getAttribute(KafkaAttrKeys.KAFKA_LOGIN_KEY);
        return certificate != null ? certificate : LoginCertificate.createUnLogin();
    }

}

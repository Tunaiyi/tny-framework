package com.tny.game.net.kafka;

import com.tny.game.common.context.AttrKey;
import com.tny.game.common.context.AttributeUtils;
import com.tny.game.net.LoginCertificate;

/**
 * Created by Kun Yang on 16/8/10.
 */
interface KafkaAttrKeys {

    AttrKey<LoginCertificate> KAFKA_LOGIN_KEY = AttributeUtils.key(KafkaAttrKeys.class, "KAFKA_LOGIN_KEY");


}

package com.tny.game.suite.net.kafka;

import com.tny.game.common.utils.md5.MD5;
import com.tny.game.net.LoginCertificate;
import com.tny.game.net.kafka.KafkaServerInfo;
import com.tny.game.net.kafka.KafkaTicket;
import com.tny.game.net.kafka.KafkaTicketSeller;
import com.tny.game.net.kafka.KafkaTicketTaker;
import com.tny.game.suite.utils.Configs;

/**
 * Created by Kun Yang on 16/8/10.
 */
public class MD5KafkaTicketConductor implements KafkaTicketSeller, KafkaTicketTaker {

    @Override
    public boolean take(KafkaTicket ticket, String checkKey) {
        return ticket.getSecret().equals(secret(ticket.getUserGroup(), ticket.getUID(),
                ticket.getRemoteType(), ticket.getRemoteID(), checkKey));
    }

    @Override
    public KafkaTicket create(KafkaServerInfo remoteServer, LoginCertificate certificate, String checkKey) {
        return new KafkaTicket(certificate.getUserGroup(), certificate.getUserID(),
                remoteServer.getServerType(), remoteServer.getID(),
                secret(certificate.getUserGroup(), certificate.getUserID(),
                        remoteServer.getServerType(), remoteServer.getID(), checkKey));
    }

    private String secret(String group, long id, String remoteType, int remoteID, String checkKey) {
        String key = Configs.createAuthKey(group);
        if (key == null)
            return null;
        return MD5.md5(group + id + remoteType + remoteID + key + checkKey);
    }

}

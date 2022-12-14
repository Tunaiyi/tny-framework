package com.tny.game.suite.login;

import com.tny.game.common.digest.rsa.*;
import com.tny.game.protoex.*;
import com.tny.game.protoex.annotations.*;
import com.tny.game.suite.utils.*;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.*;

import java.security.interfaces.*;
import java.security.spec.InvalidKeySpecException;

public class GameTicketHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameTicketHelper.class);

    private static RSAPublicKey PUBLIC_KEY;// = RSAUtils.toPublicKey(this.pubKey);

    private static RSAPrivateKey PRIVATE_KEY;// = RSAUtils.toPrivateKey(this.priKey);

    static {
        String pubKey = Configs.AUTH_CONFIG.getString(Configs.AUTH_GAME_TICKET_PUBLIC_KEY);
        String priKey = Configs.AUTH_CONFIG.getString(Configs.AUTH_GAME_TICKET_PRIVATE_KEY);
        try {
            if (pubKey != null) {
                GameTicketHelper.PUBLIC_KEY = RSAUtils.toPublicKey(pubKey);
            }
            if (priKey != null) {
                GameTicketHelper.PRIVATE_KEY = RSAUtils.toPrivateKey(priKey);
            }
        } catch (InvalidKeySpecException e) {
            LOGGER.error("{}", e);
            throw new RuntimeException(e);
        }
    }

    public static GameTicket decryptTicket(String ticketStr) throws Exception {
        byte[] decryptProtoEx = Base64.decodeBase64(ticketStr);
        byte[] deProtoExBytes = RSAUtils.decrypt(decryptProtoEx, PUBLIC_KEY);
        try (ProtoExReader reader = new ProtoExReader(deProtoExBytes)) {
            return reader.readMessage(GameTicket.class);
        }
    }

    public static String encryptTicket(GameTicket ticket) throws Exception {
        try (ProtoExWriter writer = new ProtoExWriter()) {
            writer.writeMessage(ticket, TypeEncode.EXPLICIT);
            byte[] protoExBytes = writer.toByteArray();
            byte[] encryptProtoEx = RSAUtils.encrypt(protoExBytes, PRIVATE_KEY);
            return Base64.encodeBase64String(encryptProtoEx);
        }
    }

}

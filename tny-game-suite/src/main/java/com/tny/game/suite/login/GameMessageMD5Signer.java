package com.tny.game.suite.login;

import com.google.common.collect.ImmutableSet;
import com.tny.game.common.context.Attributes;
import com.tny.game.net.base.AppConstants;
import com.tny.game.net.base.annotation.Unit;
import com.tny.game.net.common.checker.MessageMD5Signer;
import com.tny.game.net.message.Message;
import com.tny.game.net.message.MessageUtils;
import com.tny.game.net.tunnel.Tunnel;
import com.tny.game.suite.core.AttributesKeys;
import com.tny.game.suite.utils.Configs;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Unit("GameMessageMD5Signer")
public class GameMessageMD5Signer<UID> extends MessageMD5Signer<UID> {

    private short[] randomKey;

    private Set<String> checkGroups = ImmutableSet.of();

    public GameMessageMD5Signer(short[] randomKey, Set<String> checkGroups) {
        if (randomKey == null)
            this.randomKey = new short[0];
        else
            this.randomKey = Arrays.copyOf(randomKey, randomKey.length);
        this.checkGroups = ImmutableSet.copyOf(checkGroups);
    }

    public boolean isCheck(Message<?> message) {
        if (MessageUtils.isResponse(message))
            return false;
        boolean check = Configs.DEVELOP_CONFIG.getBoolean(Configs.DEVELOP_VERIFY_CHECK, true);
        return check && (checkGroups.isEmpty() || checkGroups.contains(message.getUserGroup()));
    }

    private Object getRandomKey(Message<?> message) {
        return randomKey.length > 0 ? randomKey[message.getID() % randomKey.length] : "";
    }


    private Attributes attributes(Tunnel<UID> tunnel, Message<UID> request) {
        if (tunnel != null) {
            return tunnel.attributes();
        } else {
            return request.attributes();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected String createSign(Tunnel<UID> tunnel, Message<UID> message) {
        StringBuilder signWordsBuilder = new StringBuilder(256);
        Attributes attributes = this.attributes(tunnel, message);
        String group = message.getUserGroup();
        String key;
        if (group.equals(AppConstants.DEFAULT_USER_GROUP) || group.equals(AppConstants.UNLOGIN_USER_GROUP))
            key = Configs.AUTH_CONFIG.getStr(Configs.AUTH_CLIENT_MESSAGE_KEY);
        else
            key = Configs.AUTH_CONFIG.getStr(Configs.createAuthKey(group));
        String openID = attributes.getAttribute(AttributesKeys.OPEN_ID_KEY);
        String openKey = attributes.getAttribute(AttributesKeys.OPEN_KEY_KEY);
        Object sysID = attributes.getAttribute(AttributesKeys.SYSTEM_USER_ID);
        String sysGroup = attributes.getAttribute(AttributesKeys.SYSTEM_USER_USER_GROUP);
        openID = openID == null ? "" : openID;
        openKey = openKey == null ? "" : openKey;
        sysID = sysID == null ? "" : sysID;
        sysGroup = sysGroup == null ? "" : sysGroup;
        signWordsBuilder.append(openID)
                .append(openKey)
                .append(sysID)
                .append(sysGroup)
                .append(message.getProtocol())
                .append(message.getTime())
                .append(key)
                .append(getRandomKey(message));
        Object body = message.getBody(Object.class);
        if (body instanceof List)
            ((List) body).forEach(signWordsBuilder::append);
        else
            signWordsBuilder.append(body);
        return signWordsBuilder.toString();
    }

    //     Object serverID = attributes.getAttribute(SessionKeys.SYSTEM_USER_ID);
    //     String userGroup = attributes.getAttribute(SessionKeys.SYSTEM_USER_USER_GROUP);
    //     String password = attributes.getAttribute(SessionKeys.SYSTEM_USER_PASSWORD);
    //     checkKeyBuilder.append(serverID)
    //             .append(userGroup)
    //             .append(password)
    //             .append(request.getProtocol())
    //             .append(request.getTime());
    // }
}
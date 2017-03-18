package com.tny.game.suite.login;

import com.google.common.collect.ImmutableSet;
import com.tny.game.common.context.Attributes;
import com.tny.game.net.message.Message;
import com.tny.game.net.checker.md5.MessageSignMD5Checker;
import com.tny.game.net.session.Session;
import com.tny.game.suite.core.SessionKeys;
import com.tny.game.suite.utils.Configs;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class GameMessageSignMD5Checker extends MessageSignMD5Checker {

    private short[] randomKey;

    private Set<String> checkGroups = ImmutableSet.of();

    public GameMessageSignMD5Checker(short[] randomKey, Set<String> checkGroups) {
        if (randomKey == null)
            this.randomKey = new short[0];
        else
            this.randomKey = Arrays.copyOf(randomKey, randomKey.length);
        this.checkGroups = ImmutableSet.copyOf(checkGroups);
    }

    public boolean isCheck(Message<?> message) {
        boolean check = Configs.DEVELOP_CONFIG.getBoolean(Configs.DEVELOP_VERIFY_CHECK, true);
        return check && (checkGroups.isEmpty() || checkGroups.contains(message.getUserGroup()));
    }

    private Object getRandomKey(Message<?> message) {
        return randomKey.length > 0 ? randomKey[message.getID() % randomKey.length] : "";
    }


    private Attributes attributes(Message<?> request) {
        Session session = request.getSession();
        if (session != null) {
            return session.attributes();
        } else {
            return request.attributes();
        }
    }

    @Override
    protected String createSign(Message<?> message) {
        StringBuilder signWordsBuilder = new StringBuilder(256);
        Attributes attributes = this.attributes(message);
        String group = message.getUserGroup();
        String key;
        if (group.equals(Session.DEFAULT_USER_GROUP) || group.equals(Session.UNLOGIN_USER_GROUP))
            key = Configs.AUTH_CONFIG.getStr(Configs.AUTH_CLIENT_MESSAGE_KEY);
        else
            key = Configs.AUTH_CONFIG.getStr(Configs.createAuthKey(group));
        String openID = attributes.getAttribute(SessionKeys.OPEN_ID_KEY);
        String openKey = attributes.getAttribute(SessionKeys.OPEN_KEY_KEY);
        Object sysID = attributes.getAttribute(SessionKeys.SYSTEM_USER_ID);
        String sysGroup = attributes.getAttribute(SessionKeys.SYSTEM_USER_USER_GROUP);
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

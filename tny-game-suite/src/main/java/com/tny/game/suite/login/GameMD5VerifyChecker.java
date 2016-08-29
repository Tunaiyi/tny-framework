package com.tny.game.suite.login;

import com.google.common.collect.ImmutableSet;
import com.tny.game.common.context.Attributes;
import com.tny.game.net.checker.md5.MD5VerifyChecker;
import com.tny.game.net.dispatcher.Request;
import com.tny.game.net.dispatcher.Session;
import com.tny.game.suite.core.SessionKeys;
import com.tny.game.suite.utils.Configs;

import java.util.Arrays;
import java.util.Set;

public class GameMD5VerifyChecker extends MD5VerifyChecker {

    private short[] randomKey;

    private Set<String> checkGroups = ImmutableSet.of();

    public GameMD5VerifyChecker(short[] randomKey, Set<String> checkGroups) {
        if (randomKey == null)
            this.randomKey = new short[0];
        else
            this.randomKey = Arrays.copyOf(randomKey, randomKey.length);
        this.checkGroups = ImmutableSet.copyOf(checkGroups);
    }

    @Override
    public boolean isCheck(Request request) {
        boolean check = Configs.DEVELOP_CONFIG.getBoolean(Configs.DEVELOP_VERIFY_CHECK, true);
        return check && (checkGroups.isEmpty() || checkGroups.contains(request.getUserGroup()));
    }

    public Object getRandomKey(Request request) {
        return randomKey.length > 0 ? randomKey[request.getID() % randomKey.length] : "";
    }


    private Attributes attributes(Request request) {
        Session session = request.getSession();
        if (session != null) {
            return session.attributes();
        } else {
            return request.attributes();
        }
    }

    @Override
    protected String createCheckKey(Request request) {
        StringBuilder checkKeyBuilder = new StringBuilder(256);
        Attributes attributes = this.attributes(request);
        String group = request.getUserGroup();
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
        checkKeyBuilder.append(openID)
                .append(openKey)
                .append(sysID)
                .append(sysGroup)
                .append(request.getProtocol())
                .append(request.getTime())
                .append(key)
                .append(getRandomKey(request));
        request.getParamList().forEach(checkKeyBuilder::append);
        return checkKeyBuilder.toString();
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

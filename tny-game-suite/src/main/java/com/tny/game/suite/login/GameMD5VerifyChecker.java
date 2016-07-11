package com.tny.game.suite.login;

import com.tny.game.common.context.Attributes;
import com.tny.game.common.result.ResultCode;
import com.tny.game.net.checker.md5.MD5VerifyChecker;
import com.tny.game.net.dispatcher.Request;
import com.tny.game.net.dispatcher.Session;
import com.tny.game.suite.core.SessionKeys;
import com.tny.game.suite.utils.Configs;

import java.util.Arrays;

public class GameMD5VerifyChecker extends MD5VerifyChecker {

    private short[] randomKey;


    public Object getRandomKey(Request request) {
        return randomKey.length > 0 ? randomKey[request.getID() % randomKey.length] : "";
    }

    public GameMD5VerifyChecker(short[] randomKey) {
        if (randomKey == null)
            this.randomKey = new short[0];
        else
            this.randomKey = Arrays.copyOf(randomKey, randomKey.length);
    }

    @Override
    protected boolean isCheck() {
        return Configs.DEVELOP_CONFIG.getBoolean(Configs.DEVELOP_VERIFY_CHECK, true);
    }

    @Override
    public ResultCode match(Request request) {
        if (!this.isCheck())
            return ResultCode.SUCCESS;
        return super.match(request);
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
        if (request.getUserGroup().equals(Session.DEFAULT_USER_GROUP)
                || request.getUserGroup().equals(Session.UNLOGIN_USER_GROUP)) {
            String key = Configs.AUTH_CONFIG.getStr(Configs.AUTH_CLIENT_MESSAGE_KEY);
            String openID = attributes.getAttribute(SessionKeys.OPEN_ID_KEY);
            String openKey = attributes.getAttribute(SessionKeys.OPEN_KEY_KEY);
            openID = openID == null ? "" : openID;
            openKey = openKey == null ? "" : openKey;
            checkKeyBuilder.append(openID)
                    .append(openKey)
                    .append(request.getProtocol())
                    .append(request.getTime())
                    .append(key)
                    .append(getRandomKey(request));
        } else {
            Object serverID = attributes.getAttribute(SessionKeys.SYSTEM_USER_ID);
            String userGroup = attributes.getAttribute(SessionKeys.SYSTEM_USER_USER_GROUP);
            String password = attributes.getAttribute(SessionKeys.SYSTEM_USER_PASSWORD);
            checkKeyBuilder.append(serverID)
                    .append(userGroup)
                    .append(password)
                    .append(request.getProtocol())
                    .append(request.getTime());
        }
        request.getParamList().forEach(checkKeyBuilder::append);
        return checkKeyBuilder.toString();
    }

}

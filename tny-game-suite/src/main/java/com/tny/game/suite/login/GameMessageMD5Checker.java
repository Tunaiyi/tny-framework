package com.tny.game.suite.login;

import com.google.common.collect.ImmutableSet;
import com.tny.game.net.command.checker.MessageMD5Checker;
import com.tny.game.net.message.Message;
import com.tny.game.net.message.MessageAide;
import com.tny.game.suite.utils.Configs;

import java.util.Set;

public class GameMessageMD5Checker<UID> extends MessageMD5Checker<UID> {

    private Set<String> checkGroups;

    public GameMessageMD5Checker(short[] randomKey, Set<String> checkGroups) {
        super(new GameMessageMD5Signer<>(randomKey));
        this.checkGroups = ImmutableSet.copyOf(checkGroups);
    }

    @Override
    public boolean isCheck(Message<?> message) {
        if (MessageAide.isResponse(message))
            return false;
        boolean check = Configs.DEVELOP_CONFIG.getBoolean(Configs.DEVELOP_VERIFY_CHECK, true);
        return check && (checkGroups.isEmpty() || checkGroups.contains(message.getUserGroup()));
    }

}

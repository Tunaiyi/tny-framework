package com.tny.game.suite.cache.spring;

import com.tny.game.asyndb.*;
import com.tny.game.base.item.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.suite.login.*;

import javax.annotation.Resource;
import java.util.Optional;

public class OnlineReleaseStrategyFactory implements ReleaseStrategyFactory {

    private long defaultLifeTime = 60 * 1000 * 3;

    @Resource
    private EndpointKeeperManager endpointKeeperManager;

    private String userType;

    public OnlineReleaseStrategyFactory(String userType, long defaultAddLife) {
        this.userType = userType;
        this.defaultLifeTime = defaultAddLife;
    }

    @Override
    public ReleaseStrategy createStrategy(Object object, long addLife) {
        Long playerID = null;
        if (object instanceof Item) {
            playerID = ((Item<?>) object).getPlayerId();
        } else if (object instanceof Identifier) {
            playerID = ((Identifier) object).getPlayerId();
        }
        return new LoginTimeStrategy(playerID, addLife == Long.MIN_VALUE ? defaultLifeTime : addLife);
    }

    private class LoginTimeStrategy extends TimeoutReleaseStrategy {

        private Long playerID = null;

        private LoginTimeStrategy(Long playerID, long addLife) {
            super(addLife);
            this.playerID = playerID;
        }

        @Override
        public boolean release(AsyncDBEntity entity, long releaseAt) {
            Optional<SessionKeeper<Long>> keeperOpt = endpointKeeperManager.getSessionKeeper(userType);
            if (!keeperOpt.isPresent())
                return true;
            EndpointKeeper<Long, Session<Long>> keeper = keeperOpt.get();
            return !(!super.release(entity, releaseAt) || this.playerID != null && (IDAide.isSystem(this.playerID) || keeper.isOnline(this.playerID)));
        }

    }

}

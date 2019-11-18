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
        Long playerId = null;
        if (object instanceof Item) {
            playerId = ((Item<?>) object).getPlayerId();
        } else if (object instanceof Owned) {
            playerId = ((Owned) object).getOwnerId();
        }
        return new LoginTimeStrategy(playerId, addLife == Long.MIN_VALUE ? this.defaultLifeTime : addLife);
    }

    private class LoginTimeStrategy extends TimeoutReleaseStrategy {

        private Long playerId = null;

        private LoginTimeStrategy(Long playerId, long addLife) {
            super(addLife);
            this.playerId = playerId;
        }

        @Override
        public boolean release(AsyncDBEntity entity, long releaseAt) {
            Optional<SessionKeeper<Long>> keeperOpt = OnlineReleaseStrategyFactory.this.endpointKeeperManager.getSessionKeeper(
                    OnlineReleaseStrategyFactory.this.userType);
            if (!keeperOpt.isPresent())
                return true;
            EndpointKeeper<Long, Session<Long>> keeper = keeperOpt.get();
            return !(!super.release(entity, releaseAt) ||
                     this.playerId != null && (IDAide.isSystem(this.playerId) || keeper.isOnline(this.playerId)));
        }

    }

}

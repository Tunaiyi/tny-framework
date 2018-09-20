package com.tny.game.suite.cache.spring;

import com.tny.game.asyndb.*;
import com.tny.game.base.item.*;
import com.tny.game.net.transport.*;
import com.tny.game.suite.login.IDAide;

import javax.annotation.Resource;

public class OnlineReleaseStrategyFactory implements ReleaseStrategyFactory {

    private long defaultLifeTime = 60 * 1000 * 3;

    @Resource
    private SessionKeeperFactory sessionKeeperFactory;

    private String userType;

    public OnlineReleaseStrategyFactory(String userType, long defaultAddLife) {
        this.userType = userType;
        this.defaultLifeTime = defaultAddLife;
    }

    @Override
    public ReleaseStrategy createStrategy(Object object, long addLife) {
        Long playerID = null;
        if (object instanceof Item) {
            playerID = ((Item<?>) object).getPlayerID();
        } else if (object instanceof Identifiable) {
            playerID = ((Identifiable) object).getPlayerID();
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
            SessionKeeper<Object> keeper = sessionKeeperFactory.getKeeper(userType);
            return !(!super.release(entity, releaseAt) || this.playerID != null && (IDAide.isSystem(this.playerID) || keeper.isOnline(this.playerID)));
        }

    }

}

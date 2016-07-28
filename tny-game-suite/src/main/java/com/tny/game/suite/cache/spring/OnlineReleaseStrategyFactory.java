package com.tny.game.suite.cache.spring;

import com.tny.game.asyndb.AsyncDBEntity;
import com.tny.game.asyndb.ReleaseStrategy;
import com.tny.game.asyndb.ReleaseStrategyFactory;
import com.tny.game.asyndb.TimeoutReleaseStrategy;
import com.tny.game.base.item.Identifiable;
import com.tny.game.base.item.Item;
import com.tny.game.base.item.Owner;
import com.tny.game.net.dispatcher.Session;
import com.tny.game.net.dispatcher.SessionHolder;
import com.tny.game.suite.core.GameInfo;
import org.springframework.beans.factory.annotation.Autowired;

public class OnlineReleaseStrategyFactory implements ReleaseStrategyFactory {

    private long defaultLifeTime = 60 * 1000 * 3;

    @Autowired
    private SessionHolder sessionHolder;

    public OnlineReleaseStrategyFactory(long defaultAddLife) {
        this.defaultLifeTime = defaultAddLife;
    }

    @Override
    public ReleaseStrategy createStrategy(Object object, long addLife) {
        Long playerID = null;
        if (object instanceof Item) {
            playerID = ((Item<?>) object).getPlayerID();
        } else if (object instanceof Identifiable) {
            playerID = ((Identifiable) object).getPlayerID();
        } else if (object instanceof Owner) {
            playerID = ((Owner<?>) object).getPlayerID();
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
            return !(!super.release(entity, releaseAt) || this.playerID != null && (GameInfo.isSystemID(this.playerID) || OnlineReleaseStrategyFactory.this.sessionHolder.isOnline(Session.DEFAULT_USER_GROUP, this.playerID)));
        }

    }

}

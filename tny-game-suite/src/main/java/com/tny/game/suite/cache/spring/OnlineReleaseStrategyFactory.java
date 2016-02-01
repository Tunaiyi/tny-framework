package com.tny.game.suite.cache.spring;

import com.tny.game.asyndb.AsyncDBEntity;
import com.tny.game.asyndb.ReleaseStrategy;
import com.tny.game.asyndb.ReleaseStrategyFactory;
import com.tny.game.base.item.Identifiable;
import com.tny.game.base.item.Item;
import com.tny.game.base.item.Owner;
import com.tny.game.net.dispatcher.Session;
import com.tny.game.net.dispatcher.SessionHolder;
import com.tny.game.suite.core.GameInfo;
import org.springframework.beans.factory.annotation.Autowired;

public class OnlineReleaseStrategyFactory implements ReleaseStrategyFactory {

    private long addLife = 60 * 1000 * 3;

    @Autowired
    private SessionHolder sessionHolder;

    public OnlineReleaseStrategyFactory(long addLife) {
        super();
        this.addLife = addLife;
    }

    @Override
    public ReleaseStrategy createStrategy(Object object) {
        Long playerID = null;
        if (object instanceof Item) {
            playerID = ((Item<?>) object).getPlayerID();
        } else if (object instanceof Identifiable) {
            playerID = ((Identifiable) object).getPlayerID();
        } else if (object instanceof Owner) {
            playerID = ((Owner<?, ?>) object).getPlayerID();
        }
        return new LoginTimeStrategy(object, playerID);
    }

    private class LoginTimeStrategy implements ReleaseStrategy {

        private volatile long timeOut = System.currentTimeMillis() + OnlineReleaseStrategyFactory.this.addLife;

        private Long playerID = null;

        private LoginTimeStrategy(Object object, Long playerID) {
            super();
            this.playerID = playerID;
        }

        @Override
        public boolean release(AsyncDBEntity entity) {
            if (this.playerID == null || GameInfo.isSystemID(this.playerID)
                    || OnlineReleaseStrategyFactory.this.sessionHolder.isOnline(Session.DEFAULT_USER_GROUP, this.playerID)
                    || System.currentTimeMillis() < this.timeOut)
                return false;
            return true;
        }

        @Override
        public void update() {
            this.timeOut = System.currentTimeMillis() + OnlineReleaseStrategyFactory.this.addLife;
        }

    }

}

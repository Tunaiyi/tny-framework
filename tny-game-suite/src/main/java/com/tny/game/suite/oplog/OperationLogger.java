package com.tny.game.suite.oplog;

import com.tny.game.basics.item.*;
import com.tny.game.basics.item.behavior.*;
import com.tny.game.common.concurrent.collection.*;
import com.tny.game.common.lifecycle.*;
import com.tny.game.common.utils.*;
import com.tny.game.oplog.*;
import com.tny.game.oplog.annotation.*;
import com.tny.game.oplog.log4j2.*;
import com.tny.game.oplog.record.*;
import org.apache.logging.log4j.*;
import org.springframework.beans.BeansException;
import org.springframework.context.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

import static com.tny.game.suite.SuiteProfiles.*;

/**
 * 操作日志记录器
 * Created by Kun Yang on 16/1/30.
 */
@Component
@Profile({ITEM_OPLOG, GAME})
public class OperationLogger extends AbstractOpLogger implements AppPrepareStart, ApplicationContextAware {

    private static final Logger oplogLogger = LogManager.getLogger("opTradeLogger");

    private static final Logger stuffLogger = LogManager.getLogger("stuffLogger");

    private static final IDCreator ID_CREATOR = new IDCreator(16);

    private final Map<Object, Snapper<Owned, Snapshot>> snapperMap = new CopyOnWriteMap<>();

    private final Map<Class<?>, Snapper<Owned, Snapshot>> targetClassSnapperMap = new CopyOnWriteMap<>();

    private static OpLogger instance;

    @Resource
    private OpLogFactory opLogFactory;

    @Resource
    private UserOpLogFactory userOpLogFactory;

    private ApplicationContext applicationContext;

    public static OpLogger logger() {
        return instance;
    }

    public OperationLogger() {
        if (OperationLogger.instance == null) {
            OperationLogger.instance = this;
        }
    }

    public OperationLogger(OpLogFactory opLogFactory, UserOpLogFactory userOpLogFactory) {
        this();
        this.opLogFactory = opLogFactory;
        this.userOpLogFactory = userOpLogFactory;
    }

    @Override
    protected void doLogSnapshot(Action action, Owned item, SnapperType type) {
        Snapper<Owned, Snapshot> snapper = this.getSnapper(type);
        this.doSnapshot(action, item, snapper);
    }

    @Override
    protected void doLogSnapshot(Action action, Owned item, Class<? extends Snapper> type) {
        Snapper<Owned, Snapshot> snapper = this.getSnapper(type);
        this.doSnapshot(action, item, snapper);
    }

    @Override
    protected void doLogSnapshot(Action action, Owned item) {
        Class<?> clazz = item.getClass();
        Snapper<Owned, Snapshot> snapper = this.targetClassSnapperMap.get(clazz);
        if (snapper == null) {
            SnapBy snapBy = clazz.getAnnotation(SnapBy.class);
            if (snapBy == null) {
                return;
            }
            Class<?> snapperClass = snapBy.value();
            snapper = this.getSnapper(snapperClass);
            this.targetClassSnapperMap.put(clazz, snapper);
        }
        if (snapper != null) {
            this.doSnapshot(action, item, snapper);
        }

    }

    private void doSnapshot(Action action, Owned item, Snapper<Owned, Snapshot> snapper) {
        long id = snapper.getSnapshotId(item);
        Snapshot snapshot = this.getSnapshot(item.getOwnerId(), id, action, snapper.getSnapshotType());
        if (snapshot != null) {
            this.updateSnapshot(snapper, item, snapshot);
        } else {
            snapshot = this.createSnapshot(snapper, item);
            if (snapshot != null) {
                this.logSnapshot(action, snapshot);
            }
        }
    }

    protected void updateSnapshot(Snapper<Owned, Snapshot> snapper, Owned item, Snapshot snapshot) {
        try {
            if (snapper != null) {
                snapper.update(snapshot, item);
            }
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    protected Snapshot createSnapshot(Snapper<Owned, Snapshot> snapper, Owned item) {
        try {
            return snapper.toSnapshot(item);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        return null;
    }

    @Override
    protected void doSubmit(OpLog log) {
        for (UserOpLog userOpLog : log.getUserLogs()) {
            if (!userOpLog.getStuffSettleLogs().isEmpty()) {
                try {
                    UserStuffRecord dto = new UserStuffRecord(ID_CREATOR.getHexId(), log, userOpLog);
                    LogMessage message = new LogMessage(dto);
                    stuffLogger.info(message);
                } catch (Exception e) {
                    LOGGER.error("", e);
                }
            }
            for (ActionLog actionLog : userOpLog.getActionLogs()) {
                try {
                    OperateRecord dto = new OperateRecord(ID_CREATOR.getHexId(), log, userOpLog, actionLog);
                    LogMessage message = new LogMessage(dto);
                    oplogLogger.info(message);
                } catch (Exception e) {
                    LOGGER.error("", e);
                }
            }
        }
    }

    @Override
    protected UserOpLog createUserOpLog(long playerId) {
        return this.userOpLogFactory.create(playerId);
    }

    @Override
    protected OpLog createLog() {
        return this.opLogFactory.createLog();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass(), LifecycleLevel.SYSTEM_LEVEL_10);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void prepareStart() throws Exception {
        Map<String, Snapper> snapperMap = this.applicationContext.getBeansOfType(Snapper.class);
        Map<Object, Snapper<Owned, Snapshot>> map = new HashMap<>();
        for (Snapper snapper : snapperMap.values()) {
            map.put(snapper.getSnapperType(), snapper);
            map.put(snapper.getClass(), snapper);
        }
        this.snapperMap.putAll(map);
    }

    private Snapper<Owned, Snapshot> getSnapper(Object type) {
        return this.snapperMap.get(type);
    }

}

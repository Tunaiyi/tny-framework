package com.tny.game.suite.oplog;

import com.tny.game.base.item.Identifiable;
import com.tny.game.base.item.behavior.Action;
import com.tny.game.common.utils.IDCreator;
import com.tny.game.common.utils.collection.CopyOnWriteMap;
import com.tny.game.net.initer.InitLevel;
import com.tny.game.net.initer.PerIniter;
import com.tny.game.net.initer.ServerPreStart;
import com.tny.game.oplog.AbstractOpLogger;
import com.tny.game.oplog.ActionLog;
import com.tny.game.oplog.OpLog;
import com.tny.game.oplog.OpLogger;
import com.tny.game.oplog.Snapper;
import com.tny.game.oplog.SnapperType;
import com.tny.game.oplog.Snapshot;
import com.tny.game.oplog.UserOpLog;
import com.tny.game.oplog.annotation.SnapBy;
import com.tny.game.oplog.log4j2.LogMessage;
import com.tny.game.suite.oplog.dto.OperateLogDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.tny.game.suite.SuiteProfiles.*;

/**
 * 操作日志记录器
 * Created by Kun Yang on 16/1/30.
 */
@Component
@Profile({ITEM_OPLOG, GAME})
public class OperationLogger extends AbstractOpLogger implements ServerPreStart, ApplicationContextAware {

    private static final Logger oplogLogger = LogManager.getLogger("opTradeLogger");

    private static IDCreator creator = new IDCreator(16);

    private Map<Object, Snapper<Identifiable, Snapshot>> snapperMap = new CopyOnWriteMap<>();
    private Map<Class<?>, Snapper<Identifiable, Snapshot>> targetClassSnapperMap = new CopyOnWriteMap<>();

    private static OpLogger instance;

    @Autowired
    private OpLogFactory opLogFactory;

    @Autowired
    private UserOpLogFactory userOpLogFactory;

    private ApplicationContext applicationContext;

    public static final OpLogger logger() {
        return instance;
    }

    public OperationLogger() {
        if (OperationLogger.instance == null)
            OperationLogger.instance = this;
    }

    public OperationLogger(OpLogFactory opLogFactory, UserOpLogFactory userOpLogFactory) {
        this();
        this.opLogFactory = opLogFactory;
        this.userOpLogFactory = userOpLogFactory;
    }

    @Override
    protected void doLogSnapshot(Action action, Identifiable item, SnapperType type) {
        Snapper<Identifiable, Snapshot> snapper = this.getSnapper(type);
        this.doSnapshot(action, item, snapper);
    }

    @Override
    protected void doLogSnapshot(Action action, Identifiable item, Class<? extends Snapper> type) {
        Snapper<Identifiable, Snapshot> snapper = this.getSnapper(type);
        this.doSnapshot(action, item, snapper);
    }

    @Override
    protected void doLogSnapshot(Action action, Identifiable item) {
        Class<?> clazz = item.getClass();
        Snapper<Identifiable, Snapshot> snapper = this.targetClassSnapperMap.get(clazz);
        if (snapper == null) {
            SnapBy snapBy = clazz.getAnnotation(SnapBy.class);
            if (snapBy == null)
                return;
            Class<?> snapperClass = snapBy.value();
            snapper = this.getSnapper(snapperClass);
            this.targetClassSnapperMap.put(clazz, snapper);
        }
        if (snapper != null)
            this.doSnapshot(action, item, snapper);

    }

    private void doSnapshot(Action action, Identifiable item, Snapper<Identifiable, Snapshot> snapper) {
        long id = snapper.getSnapshotID(item);
        Snapshot snapshot = this.getSnapshot(item.getPlayerID(), id, action, snapper.getSnapshotType());
        if (snapshot != null) {
            this.updateSnapshot(snapper, item, snapshot);
        } else {
            snapshot = this.createSnapshot(snapper, item);
            if (snapshot != null)
                this.logSnapshot(action, snapshot);
        }
    }

    protected void updateSnapshot(Snapper<Identifiable, Snapshot> snapper, Identifiable item, Snapshot snapshot) {
        try {
            if (snapper != null)
                snapper.update(snapshot, item);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    protected Snapshot createSnapshot(Snapper<Identifiable, Snapshot> snapper, Identifiable item) {
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
            int index = 0;
            for (ActionLog actionLog : userOpLog.getActionLogs()) {
                try {
                    OperateLogDTO dto = new OperateLogDTO(creator.getHexID(), log, userOpLog, actionLog, index++);
                    LogMessage message = new LogMessage(dto);
                    oplogLogger.info(message);
                } catch (Exception e) {
                    LOGGER.error("", e);
                }
            }
        }
    }

    @Override
    protected UserOpLog createUserOpLog(long playerID) {
        return userOpLogFactory.create(playerID);
    }

    @Override
    protected OpLog createLog() {
        return opLogFactory.createLog();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public PerIniter getIniter() {
        return PerIniter.initer(this.getClass(), InitLevel.LEVEL_10);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initialize() throws Exception {
        Map<String, Snapper> snapperMap = this.applicationContext.getBeansOfType(Snapper.class);
        Map<Object, Snapper<Identifiable, Snapshot>> map = new HashMap<>();
        for (Snapper snapper : snapperMap.values()) {
            map.put(snapper.getSnapperType(), snapper);
            map.put(snapper.getClass(), snapper);
        }
        this.snapperMap.putAll(map);
    }

    private Snapper<Identifiable, Snapshot> getSnapper(Object type) {
        return this.snapperMap.get(type);
    }

}

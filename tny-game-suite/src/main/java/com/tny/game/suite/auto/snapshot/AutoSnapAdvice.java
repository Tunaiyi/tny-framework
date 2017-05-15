package com.tny.game.suite.auto.snapshot;

import com.tny.game.base.item.Identifiable;
import com.tny.game.base.item.Manager;
import com.tny.game.base.item.behavior.Action;
import com.tny.game.common.reflect.aop.AfterReturningAdvice;
import com.tny.game.common.reflect.aop.BeforeAdvice;
import com.tny.game.common.reflect.aop.ThrowsAdvice;
import com.tny.game.common.utils.collection.CopyOnWriteMap;
import com.tny.game.event.annotation.Listener;
import com.tny.game.oplog.OpLogger;
import com.tny.game.suite.auto.AutoMethodHolder;
import com.tny.game.suite.auto.snapshot.AutoSnapMethod.SnapParamEntry;
import com.tny.game.suite.base.GameExplorer;
import com.tny.game.suite.oplog.OperationLogger;
import com.tny.game.suite.transaction.Transaction;
import com.tny.game.suite.transaction.listener.TransactionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import static com.tny.game.suite.SuiteProfiles.*;

@Listener
@Component
@Profile({AUTO, GAME})
public class AutoSnapAdvice implements TransactionListener, AfterReturningAdvice, BeforeAdvice, ThrowsAdvice {

    @Autowired
    private GameExplorer explorer;

    private static final Logger LOGGER = LoggerFactory.getLogger(AutoSnapAdvice.class);

    private AutoMethodHolder<AutoSnapMethod> methodHolder = new AutoMethodHolder<>(AutoSnapMethod::new);

    private Map<Class<?>, Manager<Object>> classManagerMap = new CopyOnWriteMap<Class<?>, Manager<Object>>();

    private static AutoSnapAdvice ADVICE = null;

    public AutoSnapAdvice() {
        AutoSnapAdvice.ADVICE = this;
    }

    public static AutoSnapAdvice getInstance() {
        return ADVICE;
    }

    @Override
    public void doBefore(Method method, Object[] args, Object target) throws Throwable {
        this.snapshot(method, args, target);
    }

    @Override
    public void doAfterReturning(Object returnValue, Method method, Object[] args, Object target) {
        this.snapshot(method, args, target);
    }

    @Override
    public void doAfterThrowing(Method method, Object[] args, Object target, Throwable cause) {
        this.snapshot(method, args, target);
    }

    private void snapshot(Method method, Object[] args, Object target) {
        try {
            AutoSnapMethod snapMethod = methodHolder.getInstance(method);
            if (snapMethod.isCanSnapShot()) {
                Action action = snapMethod.getAction(args);
                Collection<SnapParamEntry> params = snapMethod.getSnapParams(args);
                if (action != null) {
                    OperationLogger.logger().logSnapshotByClass((Identifiable) target, action, snapMethod.getSnapshotTypes());
                    for (SnapParamEntry param : params)
                        OperationLogger.logger().logSnapshotByClass((Identifiable) param.getObject(), action, param.getSnapshotTypes());
                }
            }
        } catch (Throwable e) {
            LOGGER.error("{}", method, e);
        }
    }

    @Override
    @Listener
    public void handleOpen(Transaction source) {
    }

    @Override
    @Listener
    public void handleClose(Transaction source) {
        OpLogger logger = OperationLogger.logger();
        if (logger.isLogging())
            logger.submit();
    }

    @Override
    @Listener
    public void handleRollback(Transaction source, Throwable cause) {
        OpLogger logger = OperationLogger.logger();
        if (logger.isLogging())
            logger.submit();
    }

}

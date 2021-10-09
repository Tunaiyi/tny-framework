package com.tny.game.suite.auto.snapshot;

import com.tny.game.basics.item.*;
import com.tny.game.basics.item.behavior.*;
import com.tny.game.boot.transaction.*;
import com.tny.game.boot.transaction.listener.*;
import com.tny.game.common.event.bus.annotation.*;
import com.tny.game.common.reflect.aop.*;
import com.tny.game.oplog.*;
import com.tny.game.suite.auto.*;
import com.tny.game.suite.auto.snapshot.AutoSnapMethod.*;
import com.tny.game.suite.oplog.*;
import org.slf4j.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Collection;

import static com.tny.game.suite.SuiteProfiles.*;

@Listener
@Component
@Profile({AUTO, GAME, AUTO_SNAP})
public class AutoSnapAdvice implements TransactionListener, AfterReturningAdvice, BeforeAdvice, ThrowsAdvice {

    @Resource
    private AutoSnapMethodFactory methodFactory;

    private static final Logger LOGGER = LoggerFactory.getLogger(AutoSnapAdvice.class);

    private final AutoMethodHolder<AutoSnapMethod<?>> methodHolder = new AutoMethodHolder<>();

    private static AutoSnapAdvice ADVICE = null;

    public AutoSnapAdvice() {
        AutoSnapAdvice.ADVICE = this;
    }

    public static AutoSnapAdvice getInstance() {
        return ADVICE;
    }

    @Override
    public void doBefore(Method method, Object[] args, Object target) {
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
            AutoSnapMethod<?> snapMethod = this.methodHolder.getInstance(method, this.methodFactory::create);
            if (snapMethod.isCanSnapShot()) {
                Action action = snapMethod.getAction(args);
                Collection<SnapParamEntry> params = snapMethod.getSnapParams(args);
                if (action != null) {
                    OperationLogger.logger().logSnapshotByClass((Owned)target, action, snapMethod.getSnapshotTypes());
                    for (SnapParamEntry param : params)
                        OperationLogger.logger().logSnapshotByClass((Owned)param.getObject(), action, param.getSnapshotTypes());
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
        if (logger.isLogged()) {
            logger.submit();
        }
    }

    @Override
    @Listener
    public void handleRollback(Transaction source, Throwable cause) {
        OpLogger logger = OperationLogger.logger();
        if (logger.isLogged()) {
            logger.submit();
        }
    }

}

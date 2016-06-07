package com.tny.game.suite.auto.snapshot;

import com.tny.game.base.item.behavior.Action;
import com.tny.game.oplog.Snapper;
import com.tny.game.oplog.annotation.Snap;
import com.tny.game.oplog.annotation.SnapReason;
import com.tny.game.suite.auto.AutoMethod;
import com.tny.game.suite.auto.annotation.None;
import com.tny.game.suite.base.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * 自动快照方法
 * Created by Kun Yang on 16/5/25.
 */
public class AutoSnapMethod extends AutoMethod<Snap, None, None> {

    public static final Logger LOGGER = LoggerFactory.getLogger(AutoSnapMethod.class);

    private Action action;

    private int actionParamIndex = -1;

    private Class<? extends Snapper>[] snapShotTypes;

    protected AutoSnapMethod(Method method) {
        super(method, Snap.class, null, null);
        Class<? extends Snapper>[] snapShotTypes = this.autoInvoke.value();
        SnapReason reason = method.getAnnotation(SnapReason.class);
        if (reason != null) {
            this.action = Actions.of(reason.value());
            this.snapShotTypes = snapShotTypes;
        } else {
            int paramIndex = -1;
            Class<?>[] paramClasses = method.getParameterTypes();
            int index = 0;
            for (Class<?> param : paramClasses) {
                if (Action.class.isAssignableFrom(param)) {
                    paramIndex = index;
                    break;
                }
                index++;
            }
            if (paramIndex == -1) {
                LOGGER.warn("{}.{}没有提供 Action 来源", method.getDeclaringClass(), method);
            } else {
                this.actionParamIndex = paramIndex;
                this.snapShotTypes = snapShotTypes;
            }
        }
    }

    public Class<? extends Snapper>[] getSnapShotTypes() {
        return snapShotTypes;
    }

    public Action getAction() {
        return action;
    }

    public int getActionParamIndex() {
        return actionParamIndex;
    }

    public Action getAction(Object[] param) {
        if (this.action != null)
            return this.action;
        if (param.length > this.actionParamIndex)
            return (Action) param[this.actionParamIndex];
        return null;
    }

    public boolean isCanSnapShot() {
        return this.action != null || this.actionParamIndex >= 0;
    }

}

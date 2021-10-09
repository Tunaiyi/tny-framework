package com.tny.game.suite.auto.snapshot;

import com.google.common.collect.ImmutableList;
import com.tny.game.basics.item.behavior.*;
import com.tny.game.oplog.*;
import com.tny.game.oplog.annotation.*;
import com.tny.game.suite.auto.*;
import com.tny.game.suite.auto.annotation.*;
import org.slf4j.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;

/**
 * 自动快照方法
 * Created by Kun Yang on 16/5/25.
 */
public class AutoSnapMethod<SN extends Annotation> extends AutoMethod<Snap, None, None> {

    public static final Logger LOGGER = LoggerFactory.getLogger(AutoSnapMethod.class);

    private Action action;

    private int actionParamIndex = -1;

    private Class<? extends Snapper>[] snapShotTypes;

    private List<SnapParamHolder> paramSnapsHolders;

    public AutoSnapMethod(Class<SN> snClass, Function<SN, Action> actionGetter, Method method) {
        super(method, Snap.class, null, null);
        Class<? extends Snapper>[] snapShotTypes = this.autoInvoke.value();
        SN reason = method.getAnnotation(snClass);
        List<SnapParamHolder> paramSnapsHolders = new ArrayList<>();
        Annotation[][] paramsAnnotations = method.getParameterAnnotations();
        for (int pIndex = 0; pIndex < paramsAnnotations.length; pIndex++) {
            Annotation[] paramAnnos = paramsAnnotations[pIndex];
            if (paramAnnos.length == 0) {
                continue;
            }
            for (Annotation anno : paramAnnos) {
                if (!(anno instanceof Snap)) {
                    continue;
                }
                paramSnapsHolders.add(new SnapParamHolder((Snap)anno, pIndex));
                break;
            }
        }
        this.paramSnapsHolders = paramSnapsHolders.isEmpty() ? ImmutableList.of() : paramSnapsHolders;
        if (reason != null) {
            this.action = actionGetter.apply(reason);
            this.snapShotTypes = snapShotTypes;
        } else {
            int actionIndex = -1;
            int index = 0;
            for (Class<?> param : method.getParameterTypes()) {
                if (Action.class.isAssignableFrom(param)) {
                    actionIndex = index;
                    break;
                }
                index++;
            }
            if (actionIndex == -1) {
                LOGGER.warn("{}.{}没有提供 Action 来源", method.getDeclaringClass(), method);
            } else {
                this.actionParamIndex = actionIndex;
                this.snapShotTypes = snapShotTypes;
            }
        }
    }

    public Class<? extends Snapper>[] getSnapshotTypes() {
        return this.snapShotTypes;
    }

    public Action getAction() {
        return this.action;
    }

    public int getActionParamIndex() {
        return this.actionParamIndex;
    }

    public Action getAction(Object[] param) {
        if (this.action != null) {
            return this.action;
        }
        if (param.length > this.actionParamIndex) {
            return (Action)param[this.actionParamIndex];
        }
        return null;
    }

    public Collection<SnapParamEntry> getSnapParams(Object[] args) {
        if (this.paramSnapsHolders.isEmpty()) {
            return ImmutableList.of();
        }
        Collection<SnapParamEntry> snapParamEntries = new ArrayList<>();
        this.paramSnapsHolders.forEach(holder -> {
            if (holder.index >= args.length) {
                return;
            }
            Object value = args[holder.index];
            snapParamEntries.add(new SnapParamEntry(value, holder.snap));
        });
        return snapParamEntries;
    }

    public boolean isCanSnapShot() {
        return this.action != null || this.actionParamIndex >= 0;
    }

    private static class SnapParamHolder {

        private Snap snap;
        private int index;

        private SnapParamHolder(Snap snap, int index) {
            this.snap = snap;
            this.index = index;
        }

        public Snap getSnap() {
            return this.snap;
        }

        public int getIndex() {
            return this.index;
        }

    }

    public static class SnapParamEntry {

        private Object object;
        private Snap snap;

        public SnapParamEntry(Object object, Snap snap) {
            this.object = object;
            this.snap = snap;
        }

        public Object getObject() {
            return this.object;
        }

        public Snap getSnap() {
            return this.snap;
        }

        public Class<? extends Snapper>[] getSnapshotTypes() {
            return this.snap.value();
        }

    }

}

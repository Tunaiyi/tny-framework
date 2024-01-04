/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.tny.game.basics.persistent;

import com.tny.game.basics.auto.*;
import com.tny.game.basics.item.*;
import com.tny.game.basics.persistent.annotation.*;
import com.tny.game.common.concurrent.collection.*;
import com.tny.game.common.context.*;
import com.tny.game.common.event.bus.annotation.*;
import com.tny.game.common.reflect.aop.*;
import org.slf4j.*;

import java.lang.reflect.*;
import java.util.*;

import static com.tny.game.common.utils.StringAide.*;

@GlobalEventListener
public final class AutoManageAdvice implements AfterReturningAdvice, ThrowsAdvice {

    private final GameExplorer explorer;

    private static final Logger LOGGER = LoggerFactory.getLogger(AutoManageAdvice.class);

    private static final AttrKey<Map<Object, Modify>> MODIFY_MAP = AttrKeys.key(AutoManageAdvice.class, "MODIFY_MAP");

    private final AutoMethodHolder<AutoManageMethod> methodHolder = new AutoMethodHolder<>(AutoManageMethod::new);

    private final Map<Class<?>, Manager<Object>> classManagerMap = new CopyOnWriteMap<>();

    private static volatile AutoManageAdvice ADVICE = null;

    private AutoManageAdvice(GameExplorer explorer) {
        this.explorer = explorer;
    }

    public static AutoManageAdvice init(GameExplorer explorer) {
        if (ADVICE != null) {
            return ADVICE;
        }
        synchronized (AutoManageAdvice.class) {
            if (ADVICE != null) {
                return ADVICE;
            }
            ADVICE = new AutoManageAdvice(explorer);
        }
        return ADVICE;
    }

    public static AutoManageAdvice getInstance() {
        return ADVICE;
    }

    @Override
    public void doAfterReturning(Object returnValue, Method method, Object[] args, Object target) {
        this.addModifyList(method, target, returnValue, args);
    }

    @Override
    public void doAfterThrowing(Method method, Object[] args, Object target, Throwable cause) {
        this.addModifyList(method, target, null, args);
    }

    private void addModifyList(Method method, Object target, Object returnValue, Object[] args) {
        AutoManageMethod holder = this.methodHolder.getInstance(method);
        if (holder.isAutoHandle()) {
            if (holder.isHandleInvoke()) {
                Modifiable modifiable = holder.getAutoInvoke();
                this.handleModifyList(target, modifiable.modify(), modifiable.immediately());
            }
            if (returnValue != null & holder.isHandleReturn()) {
                ModifiableReturn modifiableReturn = holder.getAutoReturn();
                this.handleModifyList(returnValue, modifiableReturn.modify(), modifiableReturn.immediately());
            }
            if (args == null || !holder.isHandleParams()) {
                return;
            }
            for (int index = 0; index < args.length; index++) {
                Object arg = args[index];
                ModifiableParam modifiableParam = holder.getAutoSaveParam(index);
                if (modifiableParam != null && arg != null) {
                    this.handleModifyList(arg, modifiableParam.modify(), modifiableParam.immediately());
                }
            }
        }
    }

    //    private boolean checkImmediately(Modify modify, boolean immediately) {
    //        return immediately || modify.equals(Modify.DELETE) || modify.equals(Modify.INSERT);
    //    }
    //
    //    private Map<Object, Modify> loadOrCreate(Transaction transaction) {
    //        Map<Object, Modify> modifyMap = transaction.attributes().getAttribute(MODIFY_MAP);
    //        if (modifyMap == null) {
    //            modifyMap = new HashMap<>();
    //            transaction.attributes().setAttribute(MODIFY_MAP, modifyMap);
    //        }
    //        return modifyMap;
    //    }

    private void handleModifyList(Object object, Modify modify, boolean immediately) {
        if (object instanceof Collection) {
            for (Object o : (Collection<?>) object) {
                this.doFlush(o, modify);
            }
        } else if (object.getClass().isArray()) {
            int length = Array.getLength(object);
            for (int index = 0; index < length; index++) {
                Object value = Array.get(object, index);
                if (value != null) {
                    this.doFlush(value, modify);
                }
            }
        } else {
            this.doFlush(object, modify);
        }
    }

    @SuppressWarnings("unchecked")
    private void doFlush(Object object, Modify modify) {
        if (object == null) {
            return;
        }
        Class<?> clazz = object.getClass();
        try {
            Manager<Object> manager = this.classManagerMap.get(clazz);
            if (manager == null) {
                ManagedBy managedBy = clazz.getAnnotation(ManagedBy.class);
                if (managedBy == null) {
                    throw new NullPointerException(format("{} 类未标记 {} 注解", clazz, ManagedBy.class));
                }
                manager = this.explorer.getManager(managedBy.manager());
                if (manager == null) {
                    throw new NullPointerException(format("{} 类找不到 {} manager", clazz, managedBy.manager()));
                }
                this.classManagerMap.put(clazz, manager);
            }
            switch (modify) {
                case DELETE:
                    manager.delete(object);
                    break;
                case SAVE:
                    manager.save(object);
                    break;
                case UPDATE:
                    manager.update(object);
                    break;
                case INSERT:
                    manager.insert(object);
                    break;
                default:
                    break;
            }
        } catch (Throwable e) {
            LOGGER.error("{}自动持久化异常", clazz, e);
        }
    }

}

package com.tny.game.suite.auto.persistent;

import com.tny.game.base.item.Manager;
import com.tny.game.common.collection.CopyOnWriteMap;
import com.tny.game.common.context.AttrKey;
import com.tny.game.common.context.AttrKeys;
import com.tny.game.common.event.annotation.Listener;
import com.tny.game.common.reflect.aop.AfterReturningAdvice;
import com.tny.game.common.reflect.aop.ThrowsAdvice;
import com.tny.game.common.utils.Logs;
import com.tny.game.net.common.dispatcher.CurrentCommand;
import com.tny.game.suite.auto.AutoMethodHolder;
import com.tny.game.suite.auto.persistent.annotation.AutoDB;
import com.tny.game.suite.auto.persistent.annotation.AutoDBBy;
import com.tny.game.suite.auto.persistent.annotation.AutoDBParam;
import com.tny.game.suite.auto.persistent.annotation.AutoDBReturn;
import com.tny.game.suite.auto.persistent.annotation.AutoOP;
import com.tny.game.suite.base.GameExplorer;
import com.tny.game.suite.transaction.Transaction;
import com.tny.game.suite.transaction.TransactionManager;
import com.tny.game.suite.transaction.listener.TransactionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Resource;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static com.tny.game.suite.SuiteProfiles.*;

@Listener
@Component
@Profile({AUTO, GAME})
public class AutoPersistentAdvice implements TransactionListener, AfterReturningAdvice, ThrowsAdvice {

    @Resource
    private GameExplorer explorer;

    private static final Logger LOGGER = LoggerFactory.getLogger(AutoPersistentAdvice.class);

    private static AttrKey<Map<Object, String>> OP_DB_MAP = AttrKeys.key(AutoPersistentAdvice.class, "OP_DB_MAP");

    private AutoMethodHolder<AutoDBMethod> methodHolder = new AutoMethodHolder<>(AutoDBMethod::new);

    private Map<Class<?>, Manager<Object>> classManagerMap = new CopyOnWriteMap<Class<?>, Manager<Object>>();

    private static AutoPersistentAdvice ADVICE = null;

    public AutoPersistentAdvice() {
        AutoPersistentAdvice.ADVICE = this;
    }

    public static AutoPersistentAdvice getInstance() {
        return ADVICE;
    }

    @Override
    public void doAfterReturning(Object returnValue, Method method, Object[] args, Object target) {
        this.addSaveList(method, target, returnValue, args);
    }

    @Override
    public void doAfterThrowing(Method method, Object[] args, Object target, Throwable cause) {
        this.addSaveList(method, target, null, args);
    }

    private void addSaveList(Method method, Object target, Object returnValue, Object[] args) {
        AutoDBMethod holder = methodHolder.getInstance(method);
        if (holder.isAutoHandle()) {
            Transaction transaction = TransactionManager.currentTransaction();
            if (transaction != null) {
                Map<Object, String> opDBMap = loadOrCreate(transaction);
                if (holder.isHandleInvoke()) {
                    AutoDB autoSave = holder.getAutoInvoke();
                    this.handleSaveList(target, opDBMap, autoSave.op(), autoSave.imm());
                }
                if (returnValue != null & holder.isHandleReturn()) {
                    AutoDBReturn autoSave = holder.getAutoReturn();
                    this.handleSaveList(returnValue, opDBMap, autoSave.op(), autoSave.imm());
                }
                if (args == null || !holder.isHandleParams())
                    return;
                for (int index = 0; index < args.length; index++) {
                    Object arg = args[index];
                    AutoDBParam autoSave = holder.getAutoSaveParam(index);
                    if (autoSave != null && arg != null)
                        this.handleSaveList(arg, opDBMap, autoSave.op(), autoSave.imm());
                }
            }
        }
    }

    private boolean checkImmediately(String operation, boolean immediately) {
        return immediately || operation.equals(AutoOP.DELETE) || operation.equals(AutoOP.INSERT);
    }

    private Map<Object, String> loadOrCreate(Transaction transaction) {
        Map<Object, String> saveMap = transaction.attributes().getAttribute(OP_DB_MAP);
        if (saveMap == null) {
            saveMap = new HashMap<>();
            transaction.attributes().setAttribute(OP_DB_MAP, saveMap);
        }
        return saveMap;
    }

    private void handleSaveList(Object object, Map<Object, String> saveMap, String operation, boolean immediately) {
        immediately = this.checkImmediately(operation, immediately);
        if (object instanceof Collection) {
            for (Object o : (Collection<?>) object) {
                if (immediately) {
                    saveMap.remove(o);
                    this.toDB(o, operation);
                } else {
                    saveMap.put(o, operation);
                }
            }
        } else if (object.getClass().isArray()) {
            int length = Array.getLength(object);
            for (int index = 0; index < length; index++) {
                Object value = Array.get(object, index);
                if (value != null) {
                    if (immediately) {
                        saveMap.remove(value);
                        this.toDB(value, operation);
                    } else {
                        saveMap.put(value, operation);
                    }
                }
            }
        } else {
            if (immediately) {
                saveMap.remove(object);
                this.toDB(object, operation);
            } else {
                saveMap.put(object, operation);
            }
        }
    }

    @Override
    @Listener
    public void handleOpen(Transaction source) {
    }

    private void toDB(Transaction transaction) {
        Map<Object, String> opDBMap = transaction.attributes().removeAttribute(OP_DB_MAP);
        if (opDBMap == null)
            return;
        for (Entry<Object, String> object : opDBMap.entrySet()) {
            this.toDB(object.getKey(), object.getValue());
        }
        if (!opDBMap.isEmpty())
            opDBMap.clear();
    }

    @SuppressWarnings("unchecked")
    private void toDB(Object object, String operation) {
        if (object == null)
            return;
        Class<?> clazz = object.getClass();
        try {
            Manager<Object> manager = this.classManagerMap.get(clazz);
            if (manager == null) {
                AutoDBBy saveBy = clazz.getAnnotation(AutoDBBy.class);
                if (saveBy == null)
                    throw new NullPointerException(Logs.format("{} 类未标记 {} 注解", clazz, AutoDBBy.class));
                manager = (Manager<Object>) this.explorer.getManager(saveBy.manager());
                if (manager == null)
                    throw new NullPointerException(Logs.format("{} 类找不到 {} manager", clazz, saveBy.manager()));
                this.classManagerMap.put(clazz, manager);
            }
            switch (operation) {
                case AutoOP.DELETE:
                    manager.delete(object);
                    break;
                case AutoOP.SAVE:
                    manager.save(object);
                    break;
                case AutoOP.UPDATE:
                    manager.update(object);
                    break;
                case AutoOP.INSERT:
                    manager.insert(object);
                    break;
                default:
                    break;
            }
        } catch (Throwable e) {
            LOGGER.error("{}自动持久化异常", clazz, e);
        }
    }

    @Override
    @Listener
    public void handleClose(Transaction source) {
        try {
            this.toDB(source);
        } catch (Throwable e) {
            CurrentCommand cmd = CurrentCommand.getCurrent();
            LOGGER.error("{} 协议[{}] handleClose 异常", source, cmd.getProtocol(), e);
        } finally {
            source.attributes().removeAttribute(OP_DB_MAP);
        }
    }

    @Override
    @Listener
    public void handleRollback(Transaction source, Throwable cause) {
        try {
            this.toDB(source);
        } catch (Throwable e) {
            CurrentCommand cmd = CurrentCommand.getCurrent();
            LOGGER.error("{} 协议[{}] handleRollback 异常", source, cmd.getProtocol(), e);
        }
    }
}

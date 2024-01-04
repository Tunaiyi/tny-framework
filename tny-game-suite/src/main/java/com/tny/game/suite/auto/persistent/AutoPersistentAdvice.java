package com.tny.game.suite.auto.persistent;

import com.tny.game.basics.item.*;
import com.tny.game.boot.transaction.*;
import com.tny.game.boot.transaction.listener.*;
import com.tny.game.common.concurrent.collection.*;
import com.tny.game.common.context.*;
import com.tny.game.common.event.bus.annotation.*;
import com.tny.game.common.reflect.aop.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.suite.auto.*;
import com.tny.game.suite.auto.persistent.annotation.*;
import com.tny.game.suite.base.*;
import org.slf4j.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.*;
import java.util.*;
import java.util.Map.Entry;

import static com.tny.game.common.utils.StringAide.*;
import static com.tny.game.suite.SuiteProfiles.*;

@Listener
@Component
@Profile({AUTO, GAME, AUTO_PERSISTENT})
public class AutoPersistentAdvice implements TransactionListener, AfterReturningAdvice, ThrowsAdvice {

    @Autowired
    private GameExplorer explorer;

    private static final Logger LOGGER = LoggerFactory.getLogger(AutoPersistentAdvice.class);

    private static AttrKey<Map<Object, String>> OP_DB_MAP = AttrKeys.key(AutoPersistentAdvice.class, "OP_DB_MAP");

    private AutoMethodHolder<AutoDBMethod> methodHolder = new AutoMethodHolder<>(AutoDBMethod::new);

    private Map<Class<?>, Manager<Object>> classManagerMap = new CopyOnWriteMap<>();

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
        AutoDBMethod holder = this.methodHolder.getInstance(method);
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
                if (args == null || !holder.isHandleParams()) {
                    return;
                }
                for (int index = 0; index < args.length; index++) {
                    Object arg = args[index];
                    AutoDBParam autoSave = holder.getAutoSaveParam(index);
                    if (autoSave != null && arg != null) {
                        this.handleSaveList(arg, opDBMap, autoSave.op(), autoSave.imm());
                    }
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
        if (opDBMap == null) {
            return;
        }
        for (Entry<Object, String> object : opDBMap.entrySet()) {
            this.toDB(object.getKey(), object.getValue());
        }
        if (!opDBMap.isEmpty()) {
            opDBMap.clear();
        }
    }

    @SuppressWarnings("unchecked")
    private void toDB(Object object, String operation) {
        if (object == null) {
            return;
        }
        Class<?> clazz = object.getClass();
        try {
            Manager<Object> manager = this.classManagerMap.get(clazz);
            if (manager == null) {
                AutoDBBy saveBy = clazz.getAnnotation(AutoDBBy.class);
                if (saveBy == null) {
                    throw new NullPointerException(format("{} 类未标记 {} 注解", clazz, AutoDBBy.class));
                }
                manager = (Manager<Object>) this.explorer.getManager(saveBy.manager());
                if (manager == null) {
                    throw new NullPointerException(format("{} 类找不到 {} manager", clazz, saveBy.manager()));
                }
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
            ControllerContext cmd = ControllerContext.getCurrent();
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
            ControllerContext cmd = ControllerContext.getCurrent();
            LOGGER.error("{} 协议[{}] handleRollback 异常", source, cmd.getProtocol(), e);
        }
    }

}

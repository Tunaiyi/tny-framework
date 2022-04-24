package com.tny.game.basics.item.xml;

import com.tny.game.basics.item.*;
import com.tny.game.basics.item.behavior.*;
import com.tny.game.basics.item.loader.*;
import com.tny.game.basics.item.model.*;
import com.tny.game.basics.log.*;
import com.tny.game.common.concurrent.collection.*;
import com.tny.game.common.reflect.proxy.*;
import com.tny.game.common.runtime.*;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.*;

import javax.annotation.PostConstruct;
import java.util.*;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;

/**
 * xml映射事物模型管理器
 *
 * @param <M>
 * @author KGTny
 */
public abstract class LoadableModelManager<M extends Model> extends BaseModelManager<M> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogName.ITEM_MANAGER);

    /**
     * 模型的实现类类型
     */
    protected Class<? extends M> modelClass;

    protected ModelLoader<M> loader;

    private final Set<String> paths = new LinkedHashSet<>();

    private final Set<Class<? extends Enum<?>>> enumClassSet = new HashSet<>();

    /**
     * 模型代理处理器Map
     */
    private final Map<Integer, WrapperProxy<M>> handlerMap = new CopyOnWriteMap<>();

    protected LoadableModelManager(
            Class<? extends M> modelClass,
            String... paths) {
        super();
        LOGGER.info("创建 {} 对象, 调用构造方法!!", this.getClass());
        this.modelClass = modelClass;
        WrapperProxyFactory.getWrapperProxyClass(this.modelClass);
        CollectionUtils.addAll(this.paths, paths);
    }

    protected LoadableModelManager(
            Class<? extends M> modelClass,
            Class<? extends Enum<?>>[] enumClasses,
            String... paths) {
        this(modelClass, paths);
        this.enumClassSet.addAll(Arrays.asList(enumClasses));
    }

    protected LoadableModelManager(
            Class<? extends M> modelClass,
            Class<? extends Enum<? extends Behavior>> behaviorClass,
            Class<? extends Enum<? extends DemandType>> demandTypeClass,
            Class<? extends Enum<? extends Action>> actionClass,
            Class<? extends Enum<? extends Ability>> abilityClass,
            Class<? extends Enum<? extends Option>> optionClass,
            String... paths) {
        this(modelClass, paths);
        LOGGER.info("创建 {} 对象, 调用构造方法!!", this.getClass());
        this.addEnumClass(behaviorClass);
        this.addEnumClass(actionClass);
        this.addEnumClass(abilityClass);
        this.addEnumClass(demandTypeClass);
        if (optionClass != null) {
            this.addEnumClass(optionClass);
        }
    }

    protected void addEnumClass(Class<? extends Enum<?>> clazz) {
        this.enumClassSet.add(clazz);
    }

    protected abstract ModelLoaderFactory loaderFactory();

    @PostConstruct
    protected void initManager() {
        if (this.loader == null) {
            this.loader = loaderFactory().createLoader(this.modelClass, this::loadAndInitModel);
            this.onLoadCreate(loader);
            this.loader.addPaths(this.paths);
            this.loader.addEnumClass(enumClassSet);
        }
        loader.load();
        synchronized (this) {
            this.parseAllComplete();
        }
    }

    @Override
    public Class<M> getModelClass() {
        return as(modelClass);
    }

    protected abstract ItemModelContext context();

    protected void onLoadCreate(ModelLoader<M> loader) {
    }

    protected void loadAndInitModel(List<M> models, String path, boolean reload) throws InstantiationException, IllegalAccessException {
        RunChecker.trace(this.getClass());
        Map<Integer, WrapperProxy<M>> handlerMap = new HashMap<>();
        Map<Integer, M> modelMap = new HashMap<>();
        Map<String, M> modelAliasMap = new HashMap<>();

        List<M> loadList = new ArrayList<>();
        ItemModelContext context = context();
        for (M model : models) {
            this.initModel(context, model);
            WrapperProxy<M> wrapperModel = this.handlerMap.get(model.getId());
            if (wrapperModel == null) {
                wrapperModel = WrapperProxyFactory.createWrapper(model);
                M proxyModel = wrapperModel.get$Wrapper();
                handlerMap.put(model.getId(), wrapperModel);
                modelMap.put(proxyModel.getId(), proxyModel);
                if (proxyModel.getAlias() != null) {
                    M old = modelAliasMap.putIfAbsent(proxyModel.getAlias(), proxyModel);
                    if (old != null) {
                        throw new IllegalArgumentException(format("{} {} 与 {} 相同的别名", this.modelClass, model.getId(), old.getId()));
                    }
                }
            } else {
                wrapperModel.set$Proxied(model);
            }
            loadList.add(wrapperModel.get$Wrapper());
            ItemModels.register(wrapperModel.get$Wrapper());
        }
        this.parseComplete(loadList);
        if (!handlerMap.isEmpty()) {
            this.handlerMap.putAll(handlerMap);
        }
        if (!modelMap.isEmpty()) {
            this.modelMap.putAll(modelMap);
        }
        if (!modelAliasMap.isEmpty()) {
            this.modelAliasMap.putAll(modelAliasMap);
        }
        synchronized (this) {
            this.parseAllComplete();
            if (reload) {
                this.reloadAllComplete();
            }
        }
        LOGGER.info("#itemModelManager# 装载 <{}> model [{}] 完成 | 耗时 {} ms", path, this.modelClass.getName(),
                RunChecker.end(this.getClass()).costTime());
    }

    protected void parseComplete(List<M> models) {
    }

    protected void reloadAllComplete() {
    }

    protected void parseAllComplete() {
    }

}

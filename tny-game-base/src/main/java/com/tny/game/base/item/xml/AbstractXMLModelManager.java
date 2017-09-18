package com.tny.game.base.item.xml;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.converters.collections.MapConverter;
import com.thoughtworks.xstream.io.xml.Xpp3DomDriver;
import com.thoughtworks.xstream.mapper.DefaultImplementationsMapper;
import com.thoughtworks.xstream.mapper.Mapper;
import com.tny.game.base.item.Ability;
import com.tny.game.base.item.AbstractModelManager;
import com.tny.game.base.item.ItemExplorer;
import com.tny.game.base.item.ItemModels;
import com.tny.game.base.item.Model;
import com.tny.game.base.item.ModelExplorer;
import com.tny.game.base.item.behavior.AbstractAwardPlan;
import com.tny.game.base.item.behavior.AbstractCostPlan;
import com.tny.game.base.item.behavior.Action;
import com.tny.game.base.item.behavior.Behavior;
import com.tny.game.base.item.behavior.DemandParam;
import com.tny.game.base.item.behavior.DemandType;
import com.tny.game.base.item.behavior.Option;
import com.tny.game.base.item.behavior.plan.SimpleAwardGroup;
import com.tny.game.base.item.behavior.plan.SimpleAwardPlan;
import com.tny.game.base.item.behavior.plan.SimpleCostPlan;
import com.tny.game.base.log.LogName;
import com.tny.game.base.module.Feature;
import com.tny.game.base.module.Module;
import com.tny.game.base.module.OpenMode;
import com.tny.game.common.RunningChecker;
import com.tny.game.common.collection.CopyOnWriteMap;
import com.tny.game.common.collection.EmptyImmutableList;
import com.tny.game.common.collection.EmptyImmutableMap;
import com.tny.game.common.collection.EmptyImmutableSet;
import com.tny.game.common.config.FileLoader;
import com.tny.game.common.formula.FormulaHolder;
import com.tny.game.common.reflect.proxy.WrapperProxy;
import com.tny.game.common.reflect.proxy.WrapperProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

/**
 * xml映射事物模型管理器
 *
 * @param <M>
 * @author KGTny
 */
public abstract class AbstractXMLModelManager<M extends Model> extends AbstractModelManager<M> {

    private static Logger LOGGER = LoggerFactory.getLogger(LogName.ITEM_MANAGER);

    private static List<String> repeatAliasList = new ArrayList<>();

    /**
     * 模型的实现类类型
     */
    protected final Class<? extends M> modelClass;

    /**
     * 事物对象管理器
     */
    @Resource
    protected ItemExplorer itemExplorer;

    /**
     * 事物对象模型管理器
     */
    @Resource
    protected ModelExplorer itemModelExplorer;

    /**
     * 文件读取器
     */
    protected List<FileLoader> fileLoaderList;

    /**
     * 模型代理处理器Map
     */
    protected Map<Integer, WrapperProxy<M>> handlerMap = new CopyOnWriteMap<>();

    private Set<Class<? extends Enum>> enumClassSet = new HashSet<>();

    private static final Class<?>[] classes = new Class<?>[]{
            Behavior.class,
            Action.class,
            Ability.class,
            DemandType.class,
            Option.class,
            DemandParam.class,
            Feature.class,
            Module.class,
            OpenMode.class
    };

    protected AbstractXMLModelManager(
            Class<? extends M> modelClass,
            String... paths) {
        super();
        LOGGER.info("创建 {} 对象, 调用构造方法!!", this.getClass());
        this.modelClass = modelClass;
        WrapperProxyFactory.getWrapperProxyClass(this.modelClass);
        this.fileLoaderList = new ArrayList<>();
        for (String path : paths) {
            this.fileLoaderList.add(new XMLFileLoader(path));
        }
    }

    protected AbstractXMLModelManager(
            Class<? extends M> modelClass,
            Class<? extends Enum<?>>[] enumClasses,
            String... paths) {
        this(modelClass, paths);
        for (Class<? extends Enum<?>> clazz : enumClasses) {
            this.enumClassSet.add(clazz);
        }
    }

    protected AbstractXMLModelManager(
            Class<? extends M> modelClass,
            Class<? extends Enum<? extends Behavior>> behaviorClass,
            Class<? extends Enum<? extends DemandType>> demandTypeClass,
            Class<? extends Enum<? extends Action>> actionClass,
            Class<? extends Enum<? extends Ability>> abilityClass,
            Class<? extends Enum<? extends Option>> optionClass,
            String... paths) {
        this(modelClass, paths);
        LOGGER.info("创建 {} 对象, 调用构造方法!!", this.getClass());
        this.enumClassSet.add(behaviorClass);
        this.enumClassSet.add(actionClass);
        this.enumClassSet.add(abilityClass);
        this.enumClassSet.add(demandTypeClass);
        if (optionClass != null)
            this.enumClassSet.add(optionClass);
    }

    @SuppressWarnings("rawtypes")
    private Map<Class<?>, List<Class<? extends Enum>>> createEnumClassMap() {
        Map<Class<?>, List<Class<? extends Enum>>> map = new HashMap<>();
        for (Class<?> interfaceClass : classes) {
            map.put(interfaceClass, new ArrayList<>());
        }
        map.put(this.getClass(), new ArrayList<>());
        return map;
    }

    protected void addEnumClass(Class<? extends Enum> clazz) {
        this.enumClassSet.add(clazz);
    }

    @PostConstruct
    protected void initManager() throws Exception {
        this.fileLoaderList.parallelStream().forEach(loader -> {
            try {
                loader.load();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
        synchronized (this) {
            this.parseAllComplete();
        }
    }

    protected XStream createXStream() {
        return new XStream(new Xpp3DomDriver());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void loadAndInitModel(String path, InputStream inputStream, boolean reload) throws IOException, InstantiationException, IllegalAccessException {
        LOGGER.info("# {} 创建 {} xstream 对象 ", this.getClass().getName(), this.modelClass.getName());
        XStream xStream = createXStream();
        Mapper mapper = xStream.getMapper().lookupMapperOfType(DefaultImplementationsMapper.class);
        xStream.addDefaultImplementation(EmptyImmutableList.class, List.class);
        xStream.addDefaultImplementation(EmptyImmutableList.class, Collection.class);
        xStream.addDefaultImplementation(EmptyImmutableSet.class, Set.class);
        xStream.addDefaultImplementation(EmptyImmutableMap.class, Map.class);

        xStream.registerConverter(new CollectionConverter(mapper) {
            @Override
            public boolean canConvert(Class type) {
                if (!super.canConvert(type))
                    return type.equals(EmptyImmutableSet.class) || type.equals(EmptyImmutableList.class);
                return true;
            }
        });
        xStream.registerConverter(new MapConverter(mapper) {
            @Override
            public boolean canConvert(Class type) {
                if (!super.canConvert(type))
                    return type.equals(EmptyImmutableMap.class);
                return true;
            }
        });

        RunningChecker.start(this.getClass());

        xStream.autodetectAnnotations(true);
        xStream.registerConverter(this.getFormulaConverter());
        xStream.registerConverter(this.getRandomConverter());
        xStream.alias("itemList", ArrayList.class);
        xStream.alias("item", this.modelClass);

        xStream.alias("alias", String.class);
        xStream.alias("demand", XMLDemand.class);

        xStream.alias("behaviorPlan", XMLBehaviorPlan.class);

        xStream.alias("actionPlan", XMLActionPlan.class);
        xStream.alias("action", Action.class);

        xStream.alias("actionOption", Entry.class);
        xStream.alias("option", Option.class);
        xStream.alias("formula", FormulaHolder.class);

        xStream.alias("costPlan", AbstractCostPlan.class, SimpleCostPlan.class);
        xStream.alias("cost", XMLDemand.class);

        xStream.alias("awardPlan", AbstractAwardPlan.class, SimpleAwardPlan.class);
        xStream.alias("awardGroupSet", TreeSet.class);
        xStream.alias("awardGroup", SimpleAwardGroup.class);
        xStream.alias("award", XMLAward.class);

        xStream.alias("tradeParam", Entry.class);
        xStream.alias("param", DemandParam.class);
        xStream.alias("formula", FormulaHolder.class);

        xStream.alias("itemAbility", Entry.class);
        xStream.alias("ability", Ability.class);

        xStream.alias("demandParam", Entry.class);
        xStream.alias("param", DemandParam.class);

        xStream.alias("entry", Entry.class);
        xStream.alias("tag", String.class);


        this.initXStream(xStream);

        Map<Class<?>, List<Class<? extends Enum>>> map = this.createEnumClassMap();
        for (Class<? extends Enum> clazz : this.enumClassSet) {
            boolean find = false;
            for (Class<?> interfaceClass : classes) {
                if (interfaceClass.isAssignableFrom(clazz)) {
                    List<Class<? extends Enum>> list = map.get(interfaceClass);
                    find = true;
                    list.add(clazz);
                    break;
                }
            }
            if (!find) {
                List<Class<? extends Enum>> list = map.get(clazz);
                if (list == null) {
                    list = new ArrayList<>();
                    map.put(clazz, list);
                }
                list.add(clazz);
            }
        }

        for (List<Class<? extends Enum>> classList : map.values()) {
            xStream.registerConverter(new String2Enum(classList));
        }

        LOGGER.info("#itemModelManager# 解析 <{}> xml ......", path);
        List<? extends M> list = this.parseModel(xStream, inputStream);
        LOGGER.info("#itemModelManager# 解析 <{}> xml 完成! ", path);

        LOGGER.info("#itemModelManager# 装载 <{}> model [{}] ......", path, this.modelClass.getName());
        List<M> models = new ArrayList<>();

        Map<Integer, WrapperProxy<M>> handlerMap = new HashMap<>();
        Map<Integer, M> modelMap = new HashMap<>();
        Map<String, M> modelAliasMap = new HashMap<>();

        for (M model : list) {
            if (model instanceof XMLItemModel) {
                ((XMLItemModel) model).init(this.itemExplorer, this.itemModelExplorer);
            } else if (model instanceof XMLModel) {
                ((XMLModel) model).init();
            }
            WrapperProxy<M> wrapperModel = this.handlerMap.get(model.getID());
            if (wrapperModel == null) {
                wrapperModel = WrapperProxyFactory.createWrapper(model);
                M proxyModel = wrapperModel.get$Wrapper();
                handlerMap.put(model.getID(), wrapperModel);
                modelMap.put(proxyModel.getID(), proxyModel);
                if (proxyModel.getAlias() != null) {
                    if (modelAliasMap.put(proxyModel.getAlias(), proxyModel) != null) {
                        repeatAliasList.add(proxyModel.getAlias());
                    }
                }
            } else {
                wrapperModel.set$Proxied(model);
            }
            models.add(wrapperModel.get$Wrapper());
            ItemModels.register(wrapperModel.get$Wrapper());
        }
        if (!handlerMap.isEmpty())
            this.handlerMap.putAll(handlerMap);
        if (!modelMap.isEmpty())
            this.modelMap.putAll(modelMap);
        if (!modelAliasMap.isEmpty())
            this.modelAliasMap.putAll(modelAliasMap);
        this.parseComplete(models);
        synchronized (this) {
            this.parseAllComplete();
            if (reload)
                this.reloadAllComplete();
        }
        LOGGER.info("#itemModelManager# 装载 <{}> model [{}] 完成 | 耗时 {} ms", path, this.modelClass.getName(), RunningChecker.end(this.getClass()).cost());
    }

    @SuppressWarnings({"unchecked"})
    private List<M> parseModel(XStream xStream, InputStream input) {
        return (List<M>) xStream.fromXML(input);
    }

    protected void initXStream(XStream xStream) {
    }

    protected void parseComplete(List<M> models) {
    }

    protected void reloadAllComplete() {
    }

    protected void parseAllComplete() {
    }

    public static void checkRepeatAlias() {
        repeatAliasList.forEach(System.out::println);
        if (!repeatAliasList.isEmpty())
            throw new IllegalArgumentException("别名出现重复");
    }

    protected abstract SingleValueConverter getFormulaConverter();

    protected abstract SingleValueConverter getRandomConverter();

    protected class XMLFileLoader extends FileLoader {

        protected XMLFileLoader(String path) {
            super(path);
        }

        @Override
        protected void doLoad(InputStream inputStream, boolean reload) throws IOException, InstantiationException, IllegalAccessException {
            AbstractXMLModelManager.this.loadAndInitModel(this.getPath(), inputStream, reload);
        }

    }

}

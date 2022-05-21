package com.tny.game.basics.item.loader.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.collections.*;
import com.thoughtworks.xstream.io.xml.Xpp3DomDriver;
import com.thoughtworks.xstream.mapper.*;
import com.tny.game.basics.item.*;
import com.tny.game.basics.item.behavior.*;
import com.tny.game.basics.item.behavior.plan.*;
import com.tny.game.basics.item.loader.*;
import com.tny.game.basics.item.model.*;
import com.tny.game.basics.item.xml.*;
import com.tny.game.basics.mould.*;
import com.tny.game.common.collection.empty.*;
import com.tny.game.common.io.config.*;
import com.tny.game.expr.*;
import org.slf4j.*;

import java.io.InputStream;
import java.util.*;
import java.util.Map.Entry;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * 模型加载器
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/17 01:38
 **/
public class XStreamModelLoader<M extends Model> implements ModelLoader<M> {

    public static final Logger LOGGER = LoggerFactory.getLogger(XStreamModelLoader.class);

    private static final List<Class<?>> classes = Arrays.asList(
            Behavior.class,
            Action.class,
            Ability.class,
            DemandType.class,
            Option.class,
            DemandParam.class,
            Feature.class,
            Mould.class,
            FeatureOpenMode.class
    );

    /**
     * 模型的实现类类型
     */
    private final Class<? extends M> modelClass;

    private final ExprHolderFactory exprHolderFactory;

    private final ModelLoadHandler<M> loadHandler;

    private ModelLoaderContextHandler<Object> contextHandler;

    private final Set<String> paths = new LinkedHashSet<>();

    private final Set<Class<? extends Enum<?>>> enumClassSet = new HashSet<>();

    /**
     * 文件读取器
     */
    private final List<FileLoader> fileLoaderList = new ArrayList<>();

    public XStreamModelLoader(Class<? extends M> modelClass, ModelLoadHandler<M> loadHandler, ExprHolderFactory exprHolderFactory) {
        this.modelClass = modelClass;
        this.exprHolderFactory = exprHolderFactory;
        this.loadHandler = loadHandler;
    }

    @Override
    public ModelLoader<M> addPath(String path) {
        this.paths.add(path);
        return this;
    }

    @Override
    public ModelLoader<M> addPaths(Collection<String> paths) {
        this.paths.addAll(paths);
        return this;
    }

    @Override
    public ModelLoader<M> addPaths(String... paths) {
        this.paths.addAll(Arrays.asList(paths));
        return this;
    }

    @Override
    public ModelLoader<M> addEnumClass(Class<? extends Enum<?>> clazz) {
        this.enumClassSet.add(clazz);
        return this;
    }

    @Override
    public ModelLoader<M> addEnumClass(Collection<Class<? extends Enum<?>>> classes) {
        this.enumClassSet.addAll(classes);
        return this;
    }

    @Override
    public <C> ModelLoader<M> setContextHandler(ModelLoaderContextHandler<C> contextHandler) {
        this.contextHandler = as(contextHandler);
        return this;
    }

    private Map<Class<?>, List<Class<? extends Enum<?>>>> createEnumClassMap() {
        Map<Class<?>, List<Class<? extends Enum<?>>>> map = new HashMap<>();
        for (Class<?> interfaceClass : classes) {
            map.put(interfaceClass, new ArrayList<>());
        }
        map.put(this.getClass(), new ArrayList<>());
        return map;
    }

    private XStream createXStream() {
        XStream xStream = new XStream(new Xpp3DomDriver());
        if (this.contextHandler != null) {
            this.contextHandler.onInit(xStream);
        }
        return xStream;
    }

    @Override
    public void load() {
        if (!fileLoaderList.isEmpty()) {
            return;
        }
        for (String path : paths) {
            this.fileLoaderList.add(new XMLFileLoader(path));
        }
        this.fileLoaderList.parallelStream().forEach(loader -> {
            try {
                loader.load();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void loadModels(String path, InputStream inputStream, boolean reload) {
        LOGGER.info("# {} 创建 {} xstream 对象 ", this.getClass().getName(), this.modelClass.getName());
        XStream xStream = createXStream();
        xStream.allowTypesByRegExp(new String[]{".*"});
        Mapper mapper = xStream.getMapper().lookupMapperOfType(DefaultImplementationsMapper.class);
        xStream.addDefaultImplementation(EmptyImmutableList.class, List.class);
        xStream.addDefaultImplementation(EmptyImmutableList.class, Collection.class);
        xStream.addDefaultImplementation(EmptyImmutableSet.class, Set.class);
        xStream.addDefaultImplementation(EmptyImmutableMap.class, Map.class);

        xStream.registerConverter(new CollectionConverter(mapper) {

            @Override
            public boolean canConvert(Class type) {
                if (!super.canConvert(type)) {
                    return type.equals(EmptyImmutableSet.class) || type.equals(EmptyImmutableList.class);
                }
                return true;
            }
        });
        xStream.registerConverter(new MapConverter(mapper) {

            @Override
            public boolean canConvert(Class type) {
                if (!super.canConvert(type)) {
                    return type.equals(EmptyImmutableMap.class);
                }
                return true;
            }
        });

        xStream.autodetectAnnotations(true);
        String2Formula exprHolderConverter = new String2Formula(exprHolderFactory);
        xStream.registerConverter(exprHolderConverter);
        String2RandomCreator randomConverter = new String2RandomCreator();
        xStream.registerConverter(randomConverter);
        xStream.alias("itemList", ArrayList.class);
        xStream.alias("item", this.modelClass);

        xStream.alias("alias", String.class);
        xStream.alias("demand", DefaultDemand.class);

        xStream.alias("behaviorPlan", DefaultBehaviorPlan.class);

        xStream.alias("actionPlan", DefaultActionPlan.class);
        xStream.alias("action", Action.class);

        xStream.alias("actionOption", Entry.class);
        xStream.alias("option", Option.class);
        xStream.alias("formula", ExprHolder.class);

        xStream.alias("costPlan", BaseCostPlan.class, DefaultCostPlan.class);
        xStream.alias("cost", DefaultDemand.class);

        xStream.alias("awardPlan", BaseAwardPlan.class, DefaultAwardPlan.class);
        xStream.alias("awardGroup", SimpleAwardGroup.class);
        xStream.alias("award", SimpleAward.class);

        xStream.alias("paramEntry", Entry.class);
        xStream.alias("tradeParam", Entry.class);
        xStream.alias("param", DemandParam.class);
        xStream.alias("formula", ExprHolder.class);

        xStream.alias("itemAbility", Entry.class);
        xStream.alias("ability", Ability.class);

        xStream.alias("demandParam", Entry.class);
        xStream.alias("param", DemandParam.class);

        xStream.alias("entry", Entry.class);
        xStream.alias("tag", String.class);

        Map<Class<?>, List<Class<? extends Enum<?>>>> map = this.createEnumClassMap();
        for (Class<? extends Enum<?>> clazz : this.enumClassSet) {
            boolean find = false;
            for (Class<?> interfaceClass : classes) {
                if (interfaceClass.isAssignableFrom(clazz)) {
                    List<Class<? extends Enum<?>>> list = map.get(interfaceClass);
                    find = true;
                    list.add(clazz);
                    break;
                }
            }
            if (!find) {
                map.computeIfAbsent(clazz, k -> new ArrayList<>()).add(clazz);
            }
        }

        for (List<Class<? extends Enum<?>>> classList : map.values()) {
            xStream.registerConverter(new String2Enum(classList));
        }

        LOGGER.info("#itemModelManager# 解析 <{}> xml ......", path);
        List<M> list = as(xStream.fromXML(inputStream));
        LOGGER.info("#itemModelManager# 解析 <{}> xml 完成! ", path);

        try {
            LOGGER.info("#itemModelManager# 装载 <{}> model [{}] ......", path, this.modelClass.getName());
            loadHandler.onLoad(list, path, reload);
            LOGGER.info("#itemModelManager# 装载 <{}> model [{}] 完成!", path, this.modelClass.getName());
        } catch (Throwable e) {
            LOGGER.info("#itemModelManager# 装载 <{}> model [{}] 异常", path, this.modelClass.getName(), e);
        }
    }

    protected class XMLFileLoader extends FileLoader {

        protected XMLFileLoader(String path) {
            super(path);
        }

        @Override
        protected void doLoad(InputStream inputStream, boolean reload) {
            XStreamModelLoader.this.loadModels(this.getPath(), inputStream, reload);
        }

    }

}

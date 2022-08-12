/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.item.loader.jackson;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.tny.game.basics.item.*;
import com.tny.game.basics.item.behavior.*;
import com.tny.game.basics.item.behavior.plan.*;
import com.tny.game.basics.item.loader.*;
import com.tny.game.basics.item.model.*;
import com.tny.game.basics.item.probability.*;
import com.tny.game.basics.mould.*;
import com.tny.game.common.collection.empty.*;
import com.tny.game.common.io.config.*;
import com.tny.game.expr.*;
import org.slf4j.*;

import java.io.*;
import java.util.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * 模型加载器
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/17 01:38
 **/
public abstract class JacksonModelLoader<M extends Model> implements ModelLoader<M> {

    public static final Logger LOGGER = LoggerFactory.getLogger(JacksonModelLoader.class);

    private volatile ObjectMapper mapper;

    private final JsonFactory factory;

    private static final List<Class<?>> classes = Arrays.asList(
            ItemType.class,
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

    public JacksonModelLoader(Class<? extends M> modelClass, ModelLoadHandler<M> loadHandler, ExprHolderFactory exprHolderFactory,
            JsonFactory factory) {
        this.factory = factory;
        this.modelClass = modelClass;
        this.exprHolderFactory = exprHolderFactory;
        this.loadHandler = loadHandler;
    }

    protected abstract String getFileType();

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

    private ObjectMapper objectMapper() {
        if (this.mapper != null) {
            return mapper;
        }
        synchronized (this) {
            if (this.mapper != null) {
                return mapper;
            }
            this.mapper = createMapper();
        }
        return mapper;
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

    private ObjectMapper createMapper() {
        LOGGER.info("# {} 创建 {} ObjectMapper 对象 ", this.getClass().getName(), this.modelClass.getName());
        ObjectMapper mapper = new ObjectMapper(factory);
        mapper.configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                .configure(MapperFeature.AUTO_DETECT_GETTERS, false)
                .configure(MapperFeature.AUTO_DETECT_IS_GETTERS, false);

        SimpleModule module = new SimpleModule();
        module.addAbstractTypeMapping(List.class, EmptyImmutableList.class);
        module.addAbstractTypeMapping(Collection.class, EmptyImmutableList.class);
        module.addAbstractTypeMapping(Set.class, EmptyImmutableSet.class);
        module.addAbstractTypeMapping(Map.class, EmptyImmutableMap.class);
        module.addDeserializer(ExprHolder.class, new ExprHolderDeserializer(exprHolderFactory));
        module.addDeserializer(RandomCreator.class, RandomCreatorDeserializer.deserializer());

        module.addAbstractTypeMapping(BaseDemand.class, DefaultDemand.class)
                .addAbstractTypeMapping(BaseBehaviorPlan.class, DefaultBehaviorPlan.class)
                .addAbstractTypeMapping(BaseActionPlan.class, DefaultActionPlan.class)
                .addAbstractTypeMapping(BaseDemand.class, DefaultDemand.class)
                .addAbstractTypeMapping(BaseAwardPlan.class, DefaultAwardPlan.class)
                .addAbstractTypeMapping(AwardGroup.class, SimpleAwardGroup.class)
                .addAbstractTypeMapping(Award.class, SimpleAward.class)
                .addAbstractTypeMapping(BaseCostPlan.class, DefaultCostPlan.class);

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
        map.forEach((key, value) -> addEnumDeserializer(module, key, value));
        mapper.registerModule(module);
        if (this.contextHandler != null) {
            this.contextHandler.onInit(mapper);
        }
        return mapper;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void loadModels(String path, InputStream inputStream, boolean reload) {
        ObjectMapper mapper = objectMapper();
        List<M> list;
        try {
            LOGGER.info("#itemModelManager# 解析 <{}> {} ......", path, getFileType());
            JavaType listType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, this.modelClass);
            list = mapper.readValue(inputStream, listType);
            LOGGER.info("#itemModelManager# 解析 <{}> {} 完成! ", path, getFileType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            LOGGER.info("#itemModelManager# 装载 <{}> model [{}] ......", path, this.modelClass.getName());
            loadHandler.onLoad(list, path, reload);
            LOGGER.info("#itemModelManager# 装载 <{}> model [{}] 完成!", path, this.modelClass.getName());
        } catch (Throwable e) {
            LOGGER.info("#itemModelManager# 装载 <{}> model [{}] 异常", path, this.modelClass.getName(), e);
        }
    }

    private <C, T> void addEnumDeserializer(SimpleModule module, Class<C> rawClass, Collection<Class<? extends T>> enumClasses) {
        EnumMapper<C> mapper = new EnumMapper<C>(rawClass, enumClasses);
        module.addKeyDeserializer(rawClass, new EnumKeyDeserializer<>(mapper));
        module.addDeserializer(rawClass, new EnumDeserializer<>(mapper));
    }

    protected class XMLFileLoader extends FileLoader {

        protected XMLFileLoader(String path) {
            super(path);
        }

        @Override
        protected void doLoad(InputStream inputStream, boolean reload) {
            JacksonModelLoader.this.loadModels(this.getPath(), inputStream, reload);
        }

    }

}

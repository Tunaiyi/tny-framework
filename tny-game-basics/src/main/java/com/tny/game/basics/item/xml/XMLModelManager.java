package com.tny.game.basics.item.xml;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.collections.*;
import com.thoughtworks.xstream.io.xml.Xpp3DomDriver;
import com.thoughtworks.xstream.mapper.*;
import com.tny.game.basics.item.*;
import com.tny.game.basics.item.behavior.*;
import com.tny.game.basics.item.behavior.plan.*;
import com.tny.game.basics.item.model.*;
import com.tny.game.basics.log.*;
import com.tny.game.basics.mould.*;
import com.tny.game.common.collection.empty.*;
import com.tny.game.common.concurrent.collection.*;
import com.tny.game.common.io.config.*;
import com.tny.game.common.reflect.proxy.*;
import com.tny.game.common.runtime.*;
import com.tny.game.expr.*;
import org.slf4j.*;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import static com.tny.game.common.utils.StringAide.*;

/**
 * xml映射事物模型管理器
 *
 * @param <M>
 * @author KGTny
 */
public abstract class XMLModelManager<M extends Model> extends BaseModelManager<M> implements SingleValueConverter {

	private static final Logger LOGGER = LoggerFactory.getLogger(LogName.ITEM_MANAGER);

	private static final Set<String> ALIAS_CHECKED_LIST = new HashSet<>();

	/**
	 * 模型的实现类类型
	 */
	protected final Class<? extends M> modelClass;

	/**
	 * 文件读取器
	 */
	protected List<FileLoader> fileLoaderList;

	/**
	 * 模型代理处理器Map
	 */
	private Map<Integer, WrapperProxy<M>> handlerMap = new CopyOnWriteMap<>();

	private final Set<Class<? extends Enum<?>>> enumClassSet = new HashSet<>();

	private static final Class<?>[] classes = new Class<?>[]{
			Behavior.class,
			Action.class,
			Ability.class,
			DemandType.class,
			Option.class,
			DemandParam.class,
			Feature.class,
			Mould.class,
			FeatureOpenMode.class
	};

	protected XMLModelManager(
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

	protected XMLModelManager(
			Class<? extends M> modelClass,
			Class<? extends Enum<?>>[] enumClasses,
			String... paths) {
		this(modelClass, paths);
		this.enumClassSet.addAll(Arrays.asList(enumClasses));
	}

	protected XMLModelManager(
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
		if (optionClass != null) {
			this.enumClassSet.add(optionClass);
		}
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

	protected void addEnumClass(Class<? extends Enum<?>> clazz) {
		this.enumClassSet.add(clazz);
	}

	@PostConstruct
	protected void initManager() {
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

	protected abstract ItemModelContext context();

	@SuppressWarnings({"unchecked", "rawtypes"})
	protected void loadAndInitModel(String path, InputStream inputStream, boolean reload)
			throws IOException, InstantiationException, IllegalAccessException {

		RunChecker.trace(this.getClass());
		Map<Integer, WrapperProxy<M>> handlerMap = new HashMap<>();
		Map<Integer, M> modelMap = new HashMap<>();
		Map<String, M> modelAliasMap = new HashMap<>();

		List<M> rawList = loadAllModels(path, inputStream);
		List<M> models = new ArrayList<>();
		ItemModelContext context = context();
		for (M model : rawList) {
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
			models.add(wrapperModel.get$Wrapper());
			ItemModels.register(wrapperModel.get$Wrapper());
		}
		if (!handlerMap.isEmpty()) {
			this.handlerMap.putAll(handlerMap);
		}
		if (!modelMap.isEmpty()) {
			this.modelMap.putAll(modelMap);
		}
		if (!modelAliasMap.isEmpty()) {
			this.modelAliasMap.putAll(modelAliasMap);
		}
		this.parseComplete(models);
		synchronized (this) {
			this.parseAllComplete();
			if (reload) {
				this.reloadAllComplete();
			}
		}
		LOGGER.info("#itemModelManager# 装载 <{}> model [{}] 完成 | 耗时 {} ms", path, this.modelClass.getName(),
				RunChecker.end(this.getClass()).costTime());
	}

	protected List<M> loadAllModels(String path, InputStream inputStream) {
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
		String2Formula exprHolderConverter = new String2Formula(this.context().getExprHolderFactory());
		xStream.registerConverter(exprHolderConverter);
		String2RandomCreator randomConverter = new String2RandomCreator();
		xStream.registerConverter(randomConverter);
		xStream.alias("itemList", ArrayList.class);
		xStream.alias("item", this.modelClass);

		xStream.alias("alias", String.class);
		xStream.alias("demand", BaseDemand.class);

		xStream.alias("behaviorPlan", BaseBehaviorPlan.class);

		xStream.alias("actionPlan", BaseActionPlan.class);
		xStream.alias("action", Action.class);

		xStream.alias("actionOption", Entry.class);
		xStream.alias("option", Option.class);
		xStream.alias("formula", ExprHolder.class);

		xStream.alias("costPlan", AbstractCostPlan.class, SimpleCostPlan.class);
		xStream.alias("cost", BaseDemand.class);

		xStream.alias("awardPlan", AbstractAwardPlan.class, SimpleAwardPlan.class);
		xStream.alias("awardGroupSet", TreeSet.class);
		xStream.alias("awardGroup", SimpleAwardGroup.class);
		xStream.alias("award", BaseAward.class);

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
				map.computeIfAbsent(clazz, k -> new ArrayList<>()).add(clazz);
			}
		}

		for (List<Class<? extends Enum>> classList : map.values()) {
			xStream.registerConverter(new String2Enum(classList));
		}

		LOGGER.info("#itemModelManager# 解析 <{}> xml ......", path);
		List<M> list = this.parseModel(xStream, inputStream);
		LOGGER.info("#itemModelManager# 解析 <{}> xml 完成! ", path);

		LOGGER.info("#itemModelManager# 装载 <{}> model [{}] ......", path, this.modelClass.getName());
		return list;
	}

	@SuppressWarnings({"unchecked"})
	private List<M> parseModel(XStream xStream, InputStream input) {
		return (List<M>)xStream.fromXML(input);
	}

	protected void initXStream(XStream xStream) {
	}

	protected void parseComplete(List<M> models) {
	}

	protected void reloadAllComplete() {
	}

	protected void parseAllComplete() {
	}

	@Override
	public String toString(Object obj) {
		return obj == null ? null : obj.toString();
	}

	@Override
	public Object fromString(String str) {
		return this.getAndCheckModelByAlias(str);
	}

	@Override
	public boolean canConvert(Class type) {
		return this.modelClass.isAssignableFrom(type);
	}

	protected class XMLFileLoader extends FileLoader {

		protected XMLFileLoader(String path) {
			super(path);
		}

		@Override
		protected void doLoad(InputStream inputStream, boolean reload) throws IOException, InstantiationException, IllegalAccessException {
			XMLModelManager.this.loadAndInitModel(this.getPath(), inputStream, reload);
		}

	}

}

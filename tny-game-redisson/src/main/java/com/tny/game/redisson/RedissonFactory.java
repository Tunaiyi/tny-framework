package com.tny.game.redisson;

import com.tny.game.common.concurrent.collection.*;
import com.tny.game.redisson.annotation.*;
import com.tny.game.redisson.exception.*;
import javassist.*;
import javassist.bytecode.*;
import javassist.bytecode.SignatureAttribute.*;
import javassist.bytecode.annotation.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 */
public class RedissonFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(RedissonFactory.class);

	private static final ClassPool classPool = ClassPool.getDefault();

	private static final Set<Class<?>> REDIS_TEMPLATE_CLASSES = new ConcurrentHashSet<>();

	private static final Map<Class<?>, TypedRedisson<?>> REDISSON_MAP = new ConcurrentHashMap<>();

	private static void importClass(Class<?> redisClass) {
		if (!REDIS_TEMPLATE_CLASSES.contains(redisClass)) {
			synchronized (REDIS_TEMPLATE_CLASSES) {
				// 重检 redisClass
				if (!REDIS_TEMPLATE_CLASSES.contains(redisClass)) {
					ClassClassPath classPath = new ClassClassPath(redisClass);
					classPool.insertClassPath(classPath);
					REDIS_TEMPLATE_CLASSES.add(redisClass);
				}
			}
		}
	}

	/**
	 * 生成 TypedRedisson 类
	 *
	 * @param persistClass 转换元类型
	 * @reurn 返回JsonTypedRedisson
	 */
	public static <T, R extends TypedRedisson<T>> R createTypedRedisson(Class<T> persistClass) {
		Class<?> redisClass = TypedRedisson.class;
		importClass(redisClass);

		TypedRedisson<?> redisson = REDISSON_MAP.get(persistClass);

		if (redisson != null) {
			return as(redisson);
		}

		synchronized (RedissonFactory.class) {

			redisson = REDISSON_MAP.get(persistClass);
			if (redisson != null) {
				return as(redisson);
			}

			R newOne = doCreate(persistClass);
			REDISSON_MAP.put(persistClass, newOne);
			return newOne;
		}
	}

	private static <T, R extends TypedRedisson<T>> R doCreate(Class<T> persistClass) {
		Class<?> redisClass = TypedRedisson.class;
		// 检测是否存在 redisClass
		RedisObject redisObject = persistClass.getAnnotation(RedisObject.class);
		String className = persistClass.getSimpleName() + redisClass.getSimpleName();
		String proxyClassName = persistClass.getPackage().getName() + "." + className;
		Class<?> proxyClass;
		try {
			proxyClass = Class.forName(proxyClassName);
		} catch (Throwable e) {
			try {
				LOGGER.info("开始生成 {} Class 的 TypedRedisson 类 : {}", persistClass, proxyClassName);
				CtClass ctClass = classPool.makeClass(proxyClassName);
				CtClass templateClass = classPool.get(redisClass.getName());
				ctClass.setSuperclass(templateClass);
				ClassType persistClassType = new ClassType(persistClass.getName());
				TypeArgument persistClassTypeArgument = new TypeArgument(persistClassType);
				ClassType superClassType = new ClassType(redisClass.getName(), new TypeArgument[]{persistClassTypeArgument});
				ClassSignature signature = new ClassSignature(null, null, new ClassType[]{superClassType});
				ctClass.setGenericSignature(signature.encode());
				CtConstructor ctConstructor = new CtConstructor(new CtClass[]{}, ctClass);
				ctConstructor.setModifiers(Modifier.PUBLIC);
				ctConstructor.setBody("{super();}");
				ctClass.addConstructor(ctConstructor);
				// 如果配置数据源
				if (StringUtils.isNotBlank(redisObject.source())) {
					// 重写 setConnectionFactory
					CtMethod setRedissonClientMethod = CtNewMethod
							.make(format("public void setRedissonClient({} redissonClient) { super.setRedissonClient($1); }",
									TypedRedisson.class.getName()), ctClass);
					ClassFile ccFile = ctClass.getClassFile();
					ConstPool constpool = ccFile.getConstPool();
					AnnotationsAttribute setConnectionFactoryMethodAttribute = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
					Annotation autowiredAnnotation = new Annotation(Autowired.class.getName(), constpool);
					Annotation qualifierAnnotation = new Annotation(Qualifier.class.getName(), constpool);
					qualifierAnnotation.addMemberValue("value", new StringMemberValue(redisObject.source(), ccFile.getConstPool()));
					setConnectionFactoryMethodAttribute.addAnnotation(autowiredAnnotation);
					setConnectionFactoryMethodAttribute.addAnnotation(qualifierAnnotation);
					setRedissonClientMethod.getMethodInfo().addAttribute(setConnectionFactoryMethodAttribute);
					ctClass.addMethod(setRedissonClientMethod);
				}
				proxyClass = ctClass.toClass(persistClass.getClassLoader(), null);
				LOGGER.info("生成 {} Class 的 TypedRedisson 类 {} 完成", persistClass, proxyClassName);
			} catch (Exception ex) {
				LOGGER.error("", ex);
				throw new GenerateClassException(format("生成 {} Class 的 TypedRedisson 类 {} 异常", persistClass, proxyClassName), ex);
			}
		}
		try {
			//            consumer.accept(persistClass, redis);
			return as(proxyClass.getConstructor().newInstance());
		} catch (Exception e) {
			throw new GenerateClassException(format("实例化 {} 异常", proxyClassName), e);
		}
	}

}

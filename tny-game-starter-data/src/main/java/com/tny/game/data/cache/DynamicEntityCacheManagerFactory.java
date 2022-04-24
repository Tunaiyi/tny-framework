package com.tny.game.data.cache;

import com.tny.game.common.concurrent.collection.*;
import com.tny.game.data.*;
import com.tny.game.data.cache.exception.*;
import javassist.*;
import javassist.bytecode.SignatureAttribute.*;
import org.slf4j.*;

import java.util.Set;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 */
public class DynamicEntityCacheManagerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicEntityCacheManagerFactory.class);

    private static final ClassPool classPool = ClassPool.getDefault();

    private static final Set<Class<?>> CLASSES = new ConcurrentHashSet<>();

    public <K extends Comparable<?>, O, R extends EntityCacheManager<K, O>> R createCache(Class<K> keyClass, Class<O> objectClass) {
        Class<?> managerClass = EntityCacheManager.class;
        // 检测是否存在 redisClass
        if (!CLASSES.contains(managerClass)) {
            synchronized (CLASSES) {
                // 重检 redisClass
                if (!CLASSES.contains(managerClass)) {
                    ClassClassPath classPath = new ClassClassPath(managerClass);
                    classPool.insertClassPath(classPath);
                    CLASSES.add(managerClass);
                }
            }
        }
        String className = objectClass.getSimpleName() + managerClass.getSimpleName();
        String proxyClassName = objectClass.getPackage().getName() + "." + className;
        Class<?> proxyClass;
        try {
            proxyClass = Class.forName(proxyClassName);
        } catch (Throwable e) {
            try {
                LOGGER.info("开始生成 Class {} 的 {} 类 : {}", objectClass, managerClass.getSimpleName(), proxyClassName);
                CtClass ctClass = classPool.makeClass(proxyClassName);
                CtClass managerCtClass = classPool.get(managerClass.getName());
                ctClass.setSuperclass(managerCtClass);

                ClassType keyClassType = new ClassType(keyClass.getName());
                TypeArgument keyClassTypeArgument = new TypeArgument(keyClassType);
                ClassType objectClassType = new ClassType(objectClass.getName());
                TypeArgument objectClassTypeArgument = new TypeArgument(objectClassType);
                ClassType superClassType = new ClassType(managerClass.getName(), new TypeArgument[]{keyClassTypeArgument, objectClassTypeArgument});
                ClassSignature signature = new ClassSignature(null, superClassType, new ClassType[]{});
                ctClass.setGenericSignature(signature.encode());

                CtConstructor ctConstructor = new CtConstructor(new CtClass[]{}, ctClass);
                ctConstructor.setModifiers(Modifier.PUBLIC);
                ctConstructor.setBody("{super();}");
                ctClass.addConstructor(ctConstructor);
                proxyClass = ctClass.toClass();
                LOGGER.info("生成 Class {} 的 {} 类完成", objectClass, proxyClassName);
            } catch (Exception ex) {
                LOGGER.error("", ex);
                throw new GenerateClassException(format("生成 Class {} 的 {} 类异常", objectClass, proxyClassName), ex);
            }
        }
        try {
            return as(proxyClass.getConstructor().newInstance());
        } catch (Exception e) {
            throw new GenerateClassException(format("实例化 {} 异常", proxyClassName), e);
        }
    }

}

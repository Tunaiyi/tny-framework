package com.tny.game.common.reflect.javassist;

import com.tny.game.common.reflect.*;
import org.slf4j.*;

import java.lang.reflect.*;
import java.util.*;

public class JSsistClassAccessor implements ClassAccessor {

    private static final Logger logger = LoggerFactory.getLogger("reflect");

    private final Class<?> javaClass;

    private final ConstructInvoker constructInvoker;

    private final Map<Method, MethodAccessor> methodMap = new HashMap<>();

    private final Map<String, PropertyAccessor> accessorMap = new HashMap<>();

    public JSsistClassAccessor(Class<?> javaClass, MethodFilter filter) {
        super();
        this.javaClass = javaClass;
        List<Method> methods = ReflectAide.getDeepMethod(this.javaClass);
        Map<String, JSsistPropertyAccessor> accessorMap = new HashMap<>();
        ConstructInvoker constructInvoker = null;
        if (!javaClass.isInterface() && !Modifier.isAbstract(javaClass.getModifiers())) {
            try {
                Constructor<?> constructor = javaClass.getDeclaredConstructor();
                constructInvoker = InvokerFactory.newConstructor(constructor);
            } catch (SecurityException e) {
                logger.error("", e);
            } catch (NoSuchMethodException e) {
                logger.warn("{} 没有默认构造方法", javaClass);
            }
        }
        this.constructInvoker = constructInvoker;
        for (Method method : methods) {
            if (method.isBridge() || Modifier.isPrivate(method.getModifiers()) ||
                    Modifier.isStatic(method.getModifiers()) || filter != null && !filter.filter(method)) {
                continue;
            }
            // 添加监听器处理器到监听器持有器
            MethodInvoker methodInvoker = InvokerFactory.newInvoker(method);
            JSsistMethodAccessor glibMethod = new JSsistMethodAccessor(method, methodInvoker);
            this.methodMap.put(method, glibMethod);
            String methodName = method.getName();
            if (method.getDeclaringClass() == Object.class) {
                continue;
            }
            Class<?> returnClazz = method.getReturnType();
            if (!methodName.equals("getClass") && methodName.startsWith("get") ||
                    methodName.startsWith("is") && (returnClazz == boolean.class || returnClazz == Boolean.class)) {
                String proName = this.getPropertyName(methodName);
                JSsistPropertyAccessor accessor = this.getAccessor(accessorMap, proName, returnClazz);
                if (accessor != null) {
                    accessor.setReader(glibMethod);
                }
            } else if (methodName.startsWith("set")) {
                Class<?>[] paramClasses = method.getParameterTypes();
                if (paramClasses.length == 1) {
                    returnClazz = paramClasses[0];
                    String proName = this.getPropertyName(methodName);
                    JSsistPropertyAccessor accessor = this.getAccessor(accessorMap, proName, returnClazz);
                    if (accessor != null) {
                        accessor.setWriter(glibMethod);
                    }
                }
            }
        }
        this.accessorMap.putAll(accessorMap);
    }

    private JSsistPropertyAccessor getAccessor(Map<String, JSsistPropertyAccessor> accessorMap, String proName, Class<?> returnClazz) {
        JSsistPropertyAccessor accessor = accessorMap.get(proName);
        if (accessor == null) {
            accessor = new JSsistPropertyAccessor();
            accessor.setName(proName);
            accessor.setType(returnClazz);
            accessorMap.put(proName, accessor);
        }
        if (!accessor.getPropertyType().equals(returnClazz) && !returnClazz.isAssignableFrom(accessor.getPropertyType())) {
            return null;
        }
        return accessor;
    }

    private String getPropertyName(String methodName) {
        String name;
        if (methodName.startsWith("g") || methodName.startsWith("s")) {
            name = methodName.substring(3);
        } else {
            name = methodName.substring(2);
        }
        if (name.equals("")) {
            return null;
        }
        Field field = ReflectAide.getDeepField(this.javaClass, name);
        if (field != null) {
            return field.getName();
        }
        if (name.equals(name.toUpperCase())) {
            return name;
        }
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }

    @Override
    public Class<?> getJavaClass() {
        return this.javaClass;
    }

    @Override
    public String getName() {
        return this.javaClass.getName();
    }

    @Override
    public Object newInstance() throws InvocationTargetException, InstantiationException {
        if (this.constructInvoker == null) {
            throw new InstantiationException(this.javaClass.toString());
        }
        return this.constructInvoker.newInstance();
    }

    @Override
    public List<MethodAccessor> getGMethodList() {
        return new ArrayList<>(this.methodMap.values());
    }

    @Override
    public MethodAccessor getMethod(Method method) {
        return this.methodMap.get(method);
    }

    @Override
    public Map<String, PropertyAccessor> getAccessorMap() {
        return Collections.unmodifiableMap(this.accessorMap);
    }

    @Override
    public PropertyAccessor getProperty(String name) {
        return this.accessorMap.get(name);
    }

    @Override
    public PropertyAccessor getProperty(String name, Class<?> clazz) {
        PropertyAccessor accessor = this.accessorMap.get(name);
        if (accessor != null && accessor.getPropertyType().equals(clazz)) {
            return accessor;
        }
        return null;
    }

    @Override
    public MethodAccessor getMethod(String name, Class<?>... parameterTypes) {
        for (MethodAccessor method : this.methodMap.values()) {
            Class<?>[] paraClasses = method.getParameterTypes();
            if (Arrays.equals(paraClasses, parameterTypes)) {
                return method;
            }
        }
        return null;
    }

}

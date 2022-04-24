package com.tny.game.common.reflect.cglib;

import com.tny.game.common.reflect.*;
import net.sf.cglib.reflect.*;

import java.lang.reflect.*;
import java.util.*;

public class CGlibClassAccessor implements ClassAccessor {

    //	private static org.slf4j.Logger logger = LoggerFactory.getLogger("reflect");

    private final Class<?> javaClass;

    private final FastClass fastClass;

    private final Map<Method, MethodAccessor> methodMap = new HashMap<>();

    private final Map<String, PropertyAccessor> accessorMap = new HashMap<>();

    protected CGlibClassAccessor(Class<?> javaClass, MethodFilter filter) {
        super();
        this.javaClass = javaClass;
        this.fastClass = FastClass.create(this.javaClass);
        List<Method> methods = ReflectAide.getDeepMethod(this.javaClass);
        Map<String, CGlibPropertyAccessor> accessorMap = new HashMap<>();
        for (Method method : methods) {
            if (!Modifier.isPublic(method.getModifiers()) || Modifier.isStatic(method.getModifiers()) || filter != null && filter.filter(method)) {
                continue;
            }
            // 添加监听器处理器到监听器持有器
            FastMethod fastMethod = this.fastClass.getMethod(method);
            CGlibMethodAccessor glibMethod = new CGlibMethodAccessor(method, fastMethod);
            this.methodMap.put(method, glibMethod);
            String methodName = method.getName();
            Class<?> returnClazz = method.getReturnType();
            if (!methodName.equals("getClass") && methodName.startsWith("get") ||
                    methodName.startsWith("is") && (returnClazz == boolean.class || returnClazz == Boolean.class)) {
                String proName = getPropertyName(methodName);
                CGlibPropertyAccessor accessor = getAccessor(accessorMap, proName, returnClazz);
                if (accessor != null) {
                    accessor.setReader(glibMethod);
                }
            } else if ((methodName.startsWith("set"))) {
                Class<?>[] paramClasses = method.getParameterTypes();
                if (paramClasses.length == 1) {
                    returnClazz = paramClasses[0];
                    String proName = getPropertyName(methodName);
                    CGlibPropertyAccessor accessor = getAccessor(accessorMap, proName, returnClazz);
                    if (accessor != null) {
                        accessor.setWriter(glibMethod);
                    }
                }
            }
        }
        //			if (entry.getValue().isReadable() && entry.getValue().isWritable()) {
        //			}
        this.accessorMap.putAll(accessorMap);
    }

    private CGlibPropertyAccessor getAccessor(Map<String, CGlibPropertyAccessor> accessorMap, String proName, Class<?> returnClazz) {
        CGlibPropertyAccessor accessor = accessorMap.get(proName);
        if (accessor == null) {
            accessor = new CGlibPropertyAccessor();
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
        return this.fastClass.getName();
    }

    @Override
    public Object newInstance() throws InvocationTargetException {
        return this.fastClass.newInstance();
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

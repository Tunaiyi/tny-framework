package com.tny.game.common.reflect.cglib;

import com.tny.game.common.reflect.*;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.Map.Entry;

public class CGlibClass implements GClass {

    //	private static org.slf4j.Logger logger = LoggerFactory.getLogger("reflect");

    private final Class<?> javaClass;

    private final FastClass fastClass;

    private final Map<Method, GMethod> methodMap = new HashMap<Method, GMethod>();

    private final Map<String, GPropertyAccessor> accessorMap = new HashMap<String, GPropertyAccessor>();

    protected CGlibClass(Class<?> javaClass, MethodFilter filter) {
        super();
        this.javaClass = javaClass;
        this.fastClass = FastClass.create(this.javaClass);
        List<Method> methods = ReflectUtils.getDeepMethod(this.javaClass);
        Map<String, CGlibPropertyAccessor> accessorMap = new HashMap<String, CGlibPropertyAccessor>();
        for (Method method : methods) {
            if (!Modifier.isPublic(method.getModifiers()) || Modifier.isStatic(method.getModifiers()) || filter != null && filter.filter(method))
                continue;
            // 添加监听器处理器到监听器持有器
            FastMethod fastMethod = this.fastClass.getMethod(method);
            CGlibMethod glibMethod = new CGlibMethod(method, fastMethod);
            methodMap.put(method, glibMethod);
            String methodName = method.getName();
            Class<?> returnClazz = method.getReturnType();
            if (!methodName.equals("getClass") && (methodName.startsWith("get") && returnClazz != null)
                    || (methodName.startsWith("is") && (returnClazz == boolean.class || returnClazz == Boolean.class))) {
                String proName = getPropertyName(methodName);
                CGlibPropertyAccessor accessor = getAccessor(accessorMap, proName, returnClazz);
                if (accessor != null)
                    accessor.setReader(glibMethod);
            } else if ((methodName.startsWith("set"))) {
                Class<?>[] paramClasses = method.getParameterTypes();
                if (paramClasses != null && paramClasses.length == 1) {
                    returnClazz = paramClasses[0];
                    String proName = getPropertyName(methodName);
                    CGlibPropertyAccessor accessor = getAccessor(accessorMap, proName, returnClazz);
                    if (accessor != null)
                        accessor.setWriter(glibMethod);
                }
            }
        }
        for (Entry<String, CGlibPropertyAccessor> entry : accessorMap.entrySet()) {
            //			if (entry.getValue().isReadable() && entry.getValue().isWritable()) {
            this.accessorMap.put(entry.getKey(), entry.getValue());
            //			}
        }
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
        String name = null;
        if (methodName.startsWith("g") || methodName.startsWith("s")) {
            name = methodName.substring(3);
        } else {
            name = methodName.substring(2);
        }
        if (name.equals(""))
            return null;
        Field field = ReflectUtils.getDeepField(javaClass, name);
        if (field != null)
            return field.getName();
        if (name.equals(name.toUpperCase()))
            return name;
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }

    @Override
    public Class<?> getJavaClass() {
        return javaClass;
    }

    @Override
    public String getName() {
        return fastClass.getName();
    }

    @Override
    public Object newInstance() throws InvocationTargetException {
        return fastClass.newInstance();
    }

    @Override
    public List<GMethod> getGMethodList() {
        return new ArrayList<GMethod>(methodMap.values());
    }

    @Override
    public GMethod getMethod(Method method) {
        return methodMap.get(method);
    }

    @Override
    public Map<String, GPropertyAccessor> getAccessorMap() {
        return Collections.unmodifiableMap(accessorMap);
    }

    @Override
    public GPropertyAccessor getProperty(String name) {
        return accessorMap.get(name);
    }

    @Override
    public GPropertyAccessor getProperty(String name, Class<?> clazz) {
        GPropertyAccessor accessor = accessorMap.get(name);
        if (accessor != null && accessor.getPropertyType().equals(clazz))
            return accessor;
        return null;
    }

    @Override
    public GMethod getMethod(String name, Class<?>... parameterTypes) {
        for (GMethod method : methodMap.values()) {
            Class<?>[] paraClasses = method.getParameterTypes();
            if (Arrays.equals(paraClasses, parameterTypes))
                return method;
        }
        return null;
    }
}

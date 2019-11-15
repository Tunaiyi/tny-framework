package com.tny.game.common.reflect;

import com.tny.game.common.event.annotation.*;
import com.tny.game.common.reflect.javassist.*;
import net.sf.cglib.reflect.*;
import org.junit.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReflectasmTest {

    Counter counter = new Counter(2.0, 1.0, 2.0);

    int times = 10000000;

    public Map<Class<?>, Annotation> map = new ConcurrentHashMap<Class<?>, Annotation>();

    @Test
    public void testGetAnn1() {
        for (Annotation annotation : counter.getClass().getAnnotations())
            map.put(annotation.getClass(), annotation);
        long time = System.currentTimeMillis();
        for (int index = 0; index < times; index++) {
            map.get(Listener.class);
        }
        System.out.println("map ann " + (System.currentTimeMillis() - time));
    }

    @Test
    public void testGetAnn2() {
        Class<?> clazz = Counter.class;
        clazz.getAnnotation(Listener.class);
        long time = System.currentTimeMillis();
        for (int index = 0; index < times; index++) {
            clazz.getAnnotation(Listener.class);
        }
        System.out.println("class get " + (System.currentTimeMillis() - time));
    }

    @Test
    public void testNomal() {
        long time = System.currentTimeMillis();
        for (int index = 0; index < times; index++)
            counter.count(80, 2L);
        System.out.println("Nomal  : " + (System.currentTimeMillis() - time));
    }

    // @Test
    // public void testNomalReflectCheck() throws Exception {
    // Method method = Counter.class.getMethod("count", new Class<?>[] {
    // int.class, long.class });
    // long time = System.currentTimeMillis();
    // for (int index = 0; index < times; index++)
    // method.invoke(counter, 80, 2L);
    // System.out.println("NomalReflectCheck  : " + (System.currentTimeMillis()
    // - time));
    // }

    //	@Test
    //	public void testNomalReflect() throws Exception {
    //		Method method = Counter.class.getMethod("count", new Class<?>[] {
    //				int.class, long.class });
    //		method.setAccessible(false);
    //		long time = System.currentTimeMillis();
    //		for (int index = 0; index < times; index++)
    //			method.invoke(counter, 80, 2L);
    //		System.out.println("NomalReflect  : "
    //				+ (System.currentTimeMillis() - time));
    //	}

    // @Test
    // public void testReflectasm() {
    // MethodAccess access = MethodAccess.get(Counter.class);
    // int addNameIndex = access.getIndex("count");
    // long time = System.currentTimeMillis();
    // for (int index = 0; index < times; index++)
    // access.invoke(counter, addNameIndex, 80, 2L);
    // System.out.println("Reflectasm Index  : " + (System.currentTimeMillis() -
    // time));
    // }

    @Test
    public void testNomalCGLIB() throws Exception {
        FastClass clazz = FastClass.create(Counter.class);
        FastMethod method = clazz.getMethod("count", new Class<?>[]{
                int.class, long.class});
        Object[] object = new Object[]{80, 2L};
        long time = System.currentTimeMillis();
        for (int index = 0; index < times; index++)
            method.invoke(counter, object);
        System.out.println("CGLIB  : " + (System.currentTimeMillis() - time));
    }

    @Test
    public void testNomalJavasisst() throws Exception {
        Method proMethod = Counter.class.getMethod("count", new Class<?>[]{
                int.class, long.class});
        MethodInvoker method = InvokerFactory.newInvoker(proMethod);
        Object[] object = new Object[]{80, 2L};
        long time = System.currentTimeMillis();
        for (int index = 0; index < times; index++)
            method.invoke(counter, object);
        System.out.println("Javasisst  : " + (System.currentTimeMillis() - time));
    }
}

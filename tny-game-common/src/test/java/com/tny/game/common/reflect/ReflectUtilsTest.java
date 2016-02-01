package com.tny.game.common.reflect;

import com.tny.game.common.reflect.ReflectUtils.MethodType;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ReflectUtilsTest {

    /**
     * @uml.property name="datas" multiplicity="(0 -1)" dimension="1"
     */
    private String[] datas = new String[]{"name", "age", "sex", "superName", "superAge", "superSex"};

    /**
     * @uml.property name="methodDada" multiplicity="(0 -1)" dimension="1"
     */
    private String[] methodDada = new String[]{"getName", "getAge", "isSex", "getSuperName", "getSuperAge",
            "isSuperSex"};

    /**
     * @uml.property name="pfields"
     * @uml.associationEnd
     */
    private String[] pfields = new String[]{"age", "sex"};

    /**
     * @uml.property name="psfields"
     * @uml.associationEnd
     */
    private String[] psfields = new String[]{"age", "sex", "superName", "superAge"};

    /**
     * @uml.property name="classes" multiplicity="(0 -1)" dimension="2"
     */
    private Class<?>[][] classes = new Class<?>[][]{{String.class}, {String.class}, {boolean.class},
            {String.class}, {String.class}, {boolean.class}};

    @Test
    public void testParseMethodName() {
        assertEquals(ReflectUtils.parseMethodName("get", "name"), "getName");
        assertEquals(ReflectUtils.parseMethodName("set", "name"), "setName");
    }

    @Test
    public void testGetDeepMethodByAnnotation() {
        List<String> getDatas = new ArrayList<String>();
        for (String name : methodDada) {
            getDatas.add(name);
        }
        List<Method> methodList = ReflectUtils.getDeepMethodByAnnotation(SuperMan.class, Pepole.class);
        assertEquals(methodList.size(), getDatas.size());
        for (Method method : methodList) {
            assertTrue(getDatas.indexOf(method.getName()) >= 0);
        }
    }

    @Test
    public void testGetDeepMethod() {
        for (String methodName : methodDada) {
            Method method = ReflectUtils.getDeepMethod(SuperMan.class, methodName);
            System.out.println(methodName + " " + method.getName());
            assertEquals(method.getName(), methodName);
        }
        int index = 0;
        for (String name : datas) {
            String methodName = ReflectUtils.parseMethodName("set", name);
            Method method = ReflectUtils.getDeepMethod(SuperMan.class, methodName, classes[index++]);
            System.out.println(methodName + " " + method.getName());
            assertEquals(method.getName(), methodName);
        }
    }

    @Test
    public void testGetPropertyMethodClassOfQMethodTypeStringClassOfQArray() {
        int index = 0;
        for (String name : datas) {
            Method method = ReflectUtils.getPropertyMethod(SuperMan.class, MethodType.Getter, name);
            System.out.println(name + " " + method.getName());
            assertEquals(method.getName(), methodDada[index++]);
        }
        index = 0;
        for (String name : datas) {
            Method method = ReflectUtils.getPropertyMethod(SuperMan.class, MethodType.Setter, name, classes[index++]);
            System.out.println(name + " " + method.getName());
            assertEquals(method.getName(), ReflectUtils.parseMethodName(MethodType.Setter.values[0], name));
        }
    }

    @Test
    public void testGetPropertyMethodClassOfQMethodTypeStringArrayClassOfQArrayArray() {
        List<Method> methodList = ReflectUtils.getPropertyMethod(SuperMan.class, MethodType.Getter, datas, null);
        int index = 0;
        for (Method method : methodList) {
            System.out.println(datas[index] + " " + method.getName());
            assertEquals(method.getName(), methodDada[index]);
            index++;
        }
        methodList = ReflectUtils.getPropertyMethod(SuperMan.class, MethodType.Setter, datas, classes);
        index = 0;
        for (Method method : methodList) {
            System.out.println(datas[index] + " " + method.getName());
            assertEquals(method.getName(),
                    ReflectUtils.parseMethodName(MethodType.Setter.values[0], datas[index]));
            index++;
        }
    }

    @Test
    public void testGetDeepFieldByAnnotation() {
        List<Field> fieldList = ReflectUtils.getDeepFieldByAnnotation(SuperMan.class, Owner.class);
        assertEquals(fieldList.size(), psfields.length);
        List<String> fieldNames = Arrays.asList(psfields);
        for (Field field : fieldList) {
            assertTrue(fieldNames.contains(field.getName()));
        }
        fieldList = ReflectUtils.getDeepFieldByAnnotation(Persion.class, Owner.class);
        assertEquals(fieldList.size(), pfields.length);
        fieldNames = Arrays.asList(pfields);
        for (Field field : fieldList) {
            assertTrue(fieldNames.contains(field.getName()));
        }
    }
}

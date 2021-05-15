package com.tny.game.common.reflect;

import com.tny.game.common.reflect.ReflectAide.*;
import org.junit.*;

import java.lang.reflect.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ReflectAideTest {

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
        assertEquals(ReflectAide.parseMethodName("get", "name"), "getName");
        assertEquals(ReflectAide.parseMethodName("set", "name"), "setName");
    }

    @Test
    public void testGetDeepMethodByAnnotation() {
        List<String> getDatas = new ArrayList<>();
        for (String name : this.methodDada) {
            getDatas.add(name);
        }
        List<Method> methodList = ReflectAide.getDeepMethodByAnnotation(SuperMan.class, Pepole.class);
        assertEquals(methodList.size(), getDatas.size());
        for (Method method : methodList) {
            assertTrue(getDatas.indexOf(method.getName()) >= 0);
        }
    }

    @Test
    public void testGetDeepMethod() {
        for (String methodName : this.methodDada) {
            Method method = ReflectAide.getDeepMethod(SuperMan.class, methodName);
            System.out.println(methodName + " " + method.getName());
            assertEquals(method.getName(), methodName);
        }
        int index = 0;
        for (String name : this.datas) {
            String methodName = ReflectAide.parseMethodName("set", name);
            Method method = ReflectAide.getDeepMethod(SuperMan.class, methodName, this.classes[index++]);
            System.out.println(methodName + " " + method.getName());
            assertEquals(method.getName(), methodName);
        }
    }

    @Test
    public void testGetPropertyMethodClassOfQMethodTypeStringClassOfQArray() {
        int index = 0;
        for (String name : this.datas) {
            Method method = ReflectAide.getPropertyMethod(SuperMan.class, MethodType.GETTER, name);
            System.out.println(name + " " + method.getName());
            assertEquals(method.getName(), this.methodDada[index++]);
        }
        index = 0;
        for (String name : this.datas) {
            Method method = ReflectAide.getPropertyMethod(SuperMan.class, MethodType.SETTER, name, this.classes[index++]);
            System.out.println(name + " " + method.getName());
            assertEquals(method.getName(), ReflectAide.parseMethodName(MethodType.SETTER.values[0], name));
        }
    }

    @Test
    public void testGetPropertyMethodClassOfQMethodTypeStringArrayClassOfQArrayArray() {
        List<Method> methodList = ReflectAide.getPropertyMethod(SuperMan.class, MethodType.GETTER, this.datas, null);
        int index = 0;
        for (Method method : methodList) {
            System.out.println(this.datas[index] + " " + method.getName());
            assertEquals(method.getName(), this.methodDada[index]);
            index++;
        }
        methodList = ReflectAide.getPropertyMethod(SuperMan.class, MethodType.SETTER, this.datas, this.classes);
        index = 0;
        for (Method method : methodList) {
            System.out.println(this.datas[index] + " " + method.getName());
            assertEquals(method.getName(),
                    ReflectAide.parseMethodName(MethodType.SETTER.values[0], this.datas[index]));
            index++;
        }
    }

    @Test
    public void testGetDeepFieldByAnnotation() {
        List<Field> fieldList = ReflectAide.getDeepFieldByAnnotation(SuperMan.class, Owner.class);
        assertEquals(fieldList.size(), this.psfields.length);
        List<String> fieldNames = Arrays.asList(this.psfields);
        for (Field field : fieldList) {
            assertTrue(fieldNames.contains(field.getName()));
        }
        fieldList = ReflectAide.getDeepFieldByAnnotation(Persion.class, Owner.class);
        assertEquals(fieldList.size(), this.pfields.length);
        fieldNames = Arrays.asList(this.pfields);
        for (Field field : fieldList) {
            assertTrue(fieldNames.contains(field.getName()));
        }
    }
}

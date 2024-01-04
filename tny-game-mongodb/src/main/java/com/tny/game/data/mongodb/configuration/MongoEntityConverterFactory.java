/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.data.mongodb.configuration;

import com.tny.game.data.mongodb.*;
import com.tny.game.data.mongodb.exception.*;
import com.tny.game.data.mongodb.loader.*;
import javassist.*;
import javassist.bytecode.*;
import javassist.bytecode.SignatureAttribute.*;
import javassist.bytecode.annotation.Annotation;
import org.bson.Document;
import org.slf4j.*;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.*;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 */
public class MongoEntityConverterFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoEntityConverterFactory.class);

    private static final ClassPool classPool = ClassPool.getDefault();

    static {
        ClassClassPath classPath = new ClassClassPath(PersistObjectLoader.class);
        classPool.insertClassPath(classPath);
    }

    /**
     * 生成Converter类
     *
     * @param persistClass 转换元类型
     * @param type         转换器类型
     * @return 返回转换器
     */
    static <S, T> Converter<S, T> createConverter(Class<?> persistClass, ConverterType type) {
        Class<?> proxyClass = createClass(persistClass, type);
        try {
            return as(proxyClass.getConstructor().newInstance());
        } catch (Exception e) {
            throw new GenerateConverterException(format("实例化 {} PersistObjectLoader 异常", persistClass), e);
        }
    }

    /**
     * 生成Conver类
     *
     * @param persistClass 转换元类型
     * @param type         转换器类型
     * @return 返回转换器
     */
    static <S, T> Converter<S, T> createConverter(Class<?> persistClass, ConverterType type, MongoDocumentConverter converter) {
        Class<?> proxyClass = createClass(persistClass, type);
        try {
            return as(proxyClass.getConstructor(MongoDocumentConverter.class).newInstance(converter));
        } catch (Exception e) {
            throw new GenerateConverterException(format("实例化 {} PersistObjectLoader 异常", persistClass), e);
        }
    }

    private static Class<?> createClass(Class<?> persistClass, ConverterType type) {
        String proxyClassName = PersistObjectLoader.class.getPackage().getName() + "." + persistClass.getSimpleName() + type.getName();
        Class<?> proxyClass;
        Class<?> sourceClass = type.getSourceType(persistClass);
        Class<?> targetClass = type.getTargetType(persistClass);
        try {
            proxyClass = Class.forName(proxyClassName);
        } catch (Throwable e) {
            try {
                LOGGER.info("开始生成 {} Class 的 {} 转换器 {}", persistClass, type, proxyClassName);
                CtClass ctClass = classPool.makeClass(proxyClassName);
                CtClass converterInterface = classPool.get(Converter.class.getName());
                // converterInterface.setGenericSignature(new SignatureAttribute.TypeVariable(Converter.class.getSimpleName() + "<" + sourceClass
                // .getName() + ", " + targetClass.getName() + ">").encode());
                ctClass.addInterface(converterInterface);

                CtClass converterClass = classPool.get(MongoDocumentConverter.class.getName());
                CtField ctField = new CtField(converterClass, "converter", ctClass);
                ctField.setModifiers(Modifier.PRIVATE);
                ctClass.addField(ctField);

                CtConstructor ctConstructor = new CtConstructor(new CtClass[]{}, ctClass);
                ctConstructor.setModifiers(Modifier.PUBLIC);
                ctConstructor.setBody("{}");
                ctClass.addConstructor(ctConstructor);

                CtConstructor ctConstructorWithConverter = new CtConstructor(new CtClass[]{converterClass}, ctClass);
                ctConstructorWithConverter.setModifiers(Modifier.PUBLIC);
                ctConstructorWithConverter.setBody("{this.converter = $1;}");
                ctClass.addConstructor(ctConstructorWithConverter);

                ClassType fromSig = new ClassType(sourceClass.getName());
                ClassType toSig = new ClassType(targetClass.getName());
                TypeArgument sourceTypeArgument = new TypeArgument(fromSig);
                TypeArgument targetTypeArgument = new TypeArgument(toSig);
                ClassType interfaceClassType = new ClassType(Converter.class.getName(), new TypeArgument[]{sourceTypeArgument, targetTypeArgument});
                ClassSignature signature = new ClassSignature(null, null, new ClassType[]{interfaceClassType});
                ctClass.setGenericSignature(signature.encode());

                // ctClass.setGenericSignature(new SignatureAttribute.TypeVariable(Converter.class.getSimpleName() + "<" + sourceClass.getName() +
                // ", " + targetClass.getName() + ">").encode());
                ClassFile ccFile = ctClass.getClassFile();
                ConstPool constpool = ccFile.getConstPool();
                AnnotationsAttribute classAttr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
                Annotation classAnnotation = new Annotation(type.getAnnotationClass().getName(), constpool);
                classAttr.addAnnotation(classAnnotation);
                ctClass.getClassFile().addAttribute(classAttr);

                CtMethod convertMethod = CtNewMethod
                        .make(format("public Object convert(Object source){return this.converter.convert($1, {}.class);}",
                                targetClass.getName()), ctClass);
                convertMethod.setModifiers(Modifier.PUBLIC);
                ctClass.addMethod(convertMethod);

                CtMethod setObjectConverterMethod = CtNewMethod
                        .make(format("public void setObjectConverter({} source){return this.converter = $1;}",
                                        MongoDocumentConverter.class.getName()),
                                ctClass);
                convertMethod.setModifiers(Modifier.PUBLIC);
                ctClass.addMethod(setObjectConverterMethod);

                proxyClass = ctClass.toClass();
                LOGGER.info("生成 {} Class 的 {} 转换器 {} 完成", persistClass, type, proxyClassName);
            } catch (Exception ex) {
                LOGGER.error("", ex);
                throw new GenerateConverterException(format("生成 {} Class 的 {} 转换器 {} 异常", persistClass, type, proxyClassName), ex);
            }
        }
        return proxyClass;
    }

    enum ConverterType {

        READ_CONVERTER("ReadConverter", Document.class, Object.class, ReadingConverter.class),

        WRITE_CONVERTER("WriteConverter", Object.class, Document.class, WritingConverter.class),

        ;

        // 名字
        private final String name;

        // 原类型
        private final Class<?> sourceType;

        // 目标类型
        private final Class<?> targetType;

        // 注解
        private final Class<?> annotationClass;

        ConverterType(String name, Class<?> sourceType, Class<?> targetType, Class<?> annotationClass) {
            this.name = name;
            this.sourceType = sourceType;
            this.targetType = targetType;
            this.annotationClass = annotationClass;
        }

        String getName() {
            return this.name;
        }

        Class<?> getSourceType(Class<?> sourceClass) {
            return this.sourceType == Object.class ? sourceClass : this.sourceType;
        }

        Class<?> getTargetType(Class<?> sourceClass) {
            return this.targetType == Object.class ? sourceClass : this.targetType;
        }

        Class<?> getAnnotationClass() {
            return this.annotationClass;
        }
    }

}

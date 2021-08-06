package com.tny.game.gradle.doc.plugin

import com.tny.game.doc.dto.DTOTableAttribute
import com.tny.game.doc.table.TableAttributeFactory
import org.gradle.api.tasks.Input

import javax.inject.Inject
import java.lang.annotation.Annotation
import java.util.function.Function

/**
 *
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/4 7:58 下午
 */
class DTOTableAttributeSpec {

    private String classAnnotation;

    private Function<Annotation, Object> classAnnotationId;

    private String fieldAnnotation;

    private Function<Annotation, Object> fieldAnnotationId;

    private ClassLoader loader = Thread.currentThread().getContextClassLoader();

    @Inject
    DTOTableAttributeSpec() {
//		try {
//			ClassLoader loader = Thread.currentThread().getContextClassLoader();
//			this.classAnnotation = as(loader.loadClass(classAnnotation));
//			this.fieldAnnotation = as(loader.loadClass(fieldAnnotation));
//		} catch (ClassNotFoundException e) {
//			throw new IllegalArgumentException(e);
//		}
//		this.classIdGetter = as(classIdGetter);
//		this.fieldIdGetter = as(fieldIdGetter);
    }

    @Input
    String getClassAnnotation() {
        return classAnnotation
    }

    @Input
    Function<Annotation, Object> getClassAnnotationId() {
        return classAnnotationId
    }

    @Input
    String getFieldAnnotation() {
        return fieldAnnotation
    }

    @Input
    Function<Annotation, Object> getFieldAnnotationId() {
        return fieldAnnotationId
    }

    void setClassAnnotation(Class<? extends Annotation> annClass) {
        this.classAnnotation = annClass.toGenericString();
    }

    void classAnnotation(String annClass) {
        this.classAnnotation = annClass;
    }

    void classAnnotation(Class<? extends Annotation> annClass) {
        this.classAnnotation = annClass.getCanonicalName();
    }

    void classIdResolver(Function<Annotation, Object> func) {
        this.classAnnotationId = func;
    }

    void setFieldAnnotation(Class<? extends Annotation> annClass) {
        this.fieldAnnotation = annClass.getCanonicalName();
    }

    void fieldAnnotation(String annClass) {
        this.fieldAnnotation = loader.loadClass(annClass) as Class<Annotation>;
    }

    void fieldAnnotation(Class<? extends Annotation> annClass) {
        this.fieldAnnotation = annClass.getCanonicalName();
    }

    void fieldIdResolver(Function<Annotation, Object> func) {
        this.fieldAnnotationId = func;
    }

    protected TableAttributeFactory createFactory() {
        return { new DTOTableAttribute(this.classAnnotation, this.classAnnotationId, this.fieldAnnotation, this.fieldAnnotationId) }
    }

}

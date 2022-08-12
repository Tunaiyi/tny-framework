/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.gradle.doc.plugin.tools.anygenerator

import com.tny.game.common.context.Attributes
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

    @Inject
    DTOTableAttributeSpec() {
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
        this.fieldAnnotation = annClass;
    }

    void fieldAnnotation(Class<? extends Annotation> annClass) {
        this.fieldAnnotation = annClass.getCanonicalName();
    }

    void fieldIdResolver(Function<Annotation, Object> func) {
        this.fieldAnnotationId = func;
    }

    protected TableAttributeFactory createFactory(Attributes attributes) {
        return { new DTOTableAttribute(this.classAnnotation, this.classAnnotationId, this.fieldAnnotation, this.fieldAnnotationId) }
    }

}

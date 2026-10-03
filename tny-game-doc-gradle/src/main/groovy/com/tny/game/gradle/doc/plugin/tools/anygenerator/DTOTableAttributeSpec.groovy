/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

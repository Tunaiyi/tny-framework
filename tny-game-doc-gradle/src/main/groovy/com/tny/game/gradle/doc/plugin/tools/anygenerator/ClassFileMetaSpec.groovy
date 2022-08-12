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

import org.springframework.core.type.AnnotationMetadata
import org.springframework.core.type.ClassMetadata
import org.springframework.core.type.classreading.MetadataReader

/**
 *
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/6 1:23 下午
 */
class ClassFileMetaSpec {

    private MetadataReader reader

    ClassMetadata getClassMeta() {
        return reader.getClassMetadata()
    }

    AnnotationMetadata getAnnotationMeta() {
        return reader.getAnnotationMetadata()
    }

    String getClassName() {
        return reader.classMetadata.className
    }

    boolean isClass(String className) {
        Objects.equals(reader.classMetadata.className, className)
    }

    boolean hasAnnotation(String annotationClass) {
        reader.getAnnotationMetadata().getAnnotationTypes().contains(annotationClass);
    }

    boolean notHasAnnotation(String annotationClass) {
        !isHasAnnotation(annotationClass)
    }

}

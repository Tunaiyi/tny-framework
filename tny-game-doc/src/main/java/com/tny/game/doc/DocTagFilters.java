/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.doc;

import com.google.common.collect.ImmutableList;
import com.tny.game.doc.annotation.*;
import com.tny.game.scanner.filter.*;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;

import java.util.*;

/**
 * Created by Kun Yang on 2016/10/13.
 */
public interface DocTagFilters {

    static ClassIncludeFilter ofInclude(boolean includeNoTag, Collection<String> groups) {
        return ClassFilterHelper.ofInclude(reader -> match(reader, includeNoTag, groups));
    }

    static ClassIncludeFilter ofInclude(boolean includeNoTag, String... groups) {
        return ofInclude(includeNoTag, ImmutableList.copyOf(groups));
    }

    static ClassExcludeFilter ofExclude(boolean includeNoTag, Collection<String> groups) {
        return ClassFilterHelper.ofExclude(reader -> match(reader, includeNoTag, groups));
    }

    static ClassExcludeFilter ofExclude(boolean includeNoTag, String... groups) {
        return ofExclude(includeNoTag, ImmutableList.copyOf(groups));
    }

    static boolean match(MetadataReader reader, boolean includeNoTag, Collection<String> groups) {
        AnnotationMetadata annotationMetadata = reader.getAnnotationMetadata();
        Set<String> names = annotationMetadata.getAnnotationTypes();
        if (names.contains(DocTag.class.getName())) {
            Map<String, Object> group = annotationMetadata.getAnnotationAttributes(DocTags.class.getName());
            String[] annoGroups = (String[])group.get("value");
            for (String g : annoGroups) {
                if (groups.contains(g)) {
                    return true;
                }
            }
        } else {
            if (includeNoTag) {
                return true;
            }
        }
        return false;
    }

}

package com.tny.game.doc;

import com.google.common.collect.ImmutableList;
import com.tny.game.doc.annotation.GroupDoc;
import com.tny.game.scanner.filter.ClassExcludeFilter;
import com.tny.game.scanner.filter.ClassFilterHelper;
import com.tny.game.scanner.filter.ClassIncludeFilter;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by Kun Yang on 2016/10/13.
 */
public interface GroupDocFilters {

    static ClassIncludeFilter ofInclude(Collection<String> groups) {
        return ClassFilterHelper.ofInclude(reader -> match(reader, groups));
    }

    static ClassIncludeFilter ofInclude(String... groups) {
        return ofInclude(ImmutableList.copyOf(groups));
    }

    static ClassExcludeFilter ofExclude(Collection<String> groups) {
        return ClassFilterHelper.ofExclude(reader -> match(reader, groups));
    }

    static ClassExcludeFilter ofExclude(String... groups) {
        return ofExclude(ImmutableList.copyOf(groups));
    }

    static boolean match(MetadataReader reader, Collection<String> groups) {
        String superClassName = reader.getClassMetadata().getSuperClassName();
        if (superClassName == null || !superClassName.equals("java.lang.Enum"))
            return false;
        AnnotationMetadata annotationMetadata = reader.getAnnotationMetadata();
        Set<String> names = annotationMetadata.getAnnotationTypes();
        if (names.contains(GroupDoc.class.getName())) {
            Map<String, Object> group = annotationMetadata.getAnnotationAttributes(GroupDoc.class.getName());
            String[] annoGroups = (String[]) group.get("value");
            for (String g : annoGroups) {
                if (groups.contains(g))
                    return true;
            }
        }
        return false;
    }

}

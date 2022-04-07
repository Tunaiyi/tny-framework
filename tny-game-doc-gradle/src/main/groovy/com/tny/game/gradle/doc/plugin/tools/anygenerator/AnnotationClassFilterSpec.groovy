package com.tny.game.gradle.doc.plugin.tools.anygenerator

import com.tny.game.scanner.filter.AnnotationClassFilter
import com.tny.game.scanner.filter.ClassFilter

import javax.inject.Inject

/**
 *
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/4 7:58 下午
 */
class AnnotationClassFilterSpec extends BaseClassFilterSpec {

    @Inject
    AnnotationClassFilterSpec() {
    }

    protected ClassFilter filter() {
        return AnnotationClassFilter.of(this.includeClasses, this.excludeClasses);
    }

}

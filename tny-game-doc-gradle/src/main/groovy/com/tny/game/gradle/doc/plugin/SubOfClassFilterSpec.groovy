package com.tny.game.gradle.doc.plugin


import com.tny.game.scanner.filter.ClassFilter
import com.tny.game.scanner.filter.SubOfClassFilter

import javax.inject.Inject

/**
 *
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/4 7:58 下午
 */
class SubOfClassFilterSpec extends BaseClassFilterSpec {

    @Inject
    SubOfClassFilterSpec() {
    }

    protected ClassFilter filter() {
        return SubOfClassFilter.of(this.includeClasses, this.excludeClasses)
    }

}

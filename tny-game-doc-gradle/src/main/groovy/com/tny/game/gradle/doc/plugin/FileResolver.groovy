package com.tny.game.gradle.doc.plugin

import com.tny.game.doc.output.PathResolver
import org.gradle.api.tasks.InputFile

/**
 *
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/4 4:55 上午
 */
interface FileResolver extends PathResolver {

    @InputFile
    @Override
    File resolve(Class<?> clazz);

}
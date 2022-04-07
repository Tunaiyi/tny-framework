package com.tny.game.gradle.doc.plugin.tools.anygenerator


import org.gradle.api.tasks.InputFile

/**
 *
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/4 4:55 上午
 */
interface FilePathResolver {

    @InputFile
    Object resolve(Class<?> clazz);

}
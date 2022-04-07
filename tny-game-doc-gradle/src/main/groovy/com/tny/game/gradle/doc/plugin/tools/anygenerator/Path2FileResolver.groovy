package com.tny.game.gradle.doc.plugin.tools.anygenerator

/**
 *
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/5 8:52 下午
 */
class Path2FileResolver implements FileResolver {

    private FilePathResolver resolver;

    Path2FileResolver(FilePathResolver resolver) {
        this.resolver = resolver
    }

    @Override
    File resolve(Class<?> clazz) {
        def value = resolver.resolve(clazz)
        if (value instanceof File)
            return file
        return new File(value.toString())
    }
}

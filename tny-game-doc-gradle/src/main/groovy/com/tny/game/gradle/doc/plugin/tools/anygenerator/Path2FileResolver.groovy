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

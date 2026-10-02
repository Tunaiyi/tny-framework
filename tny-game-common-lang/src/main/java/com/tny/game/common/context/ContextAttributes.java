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

package com.tny.game.common.context;

public class ContextAttributes extends AbstractAttributes {

    private static final Attributes EMPTY_ONE = new EmptyAttributes();

    public static Attributes empty() {
        return EMPTY_ONE;
    }

    public static Attributes create() {
        return new ContextAttributes();
    }

    public static Attributes create(AttrEntry<?>... entries) {
        return new ContextAttributes(entries);
    }

    private ContextAttributes(AttrEntry<?>... entries) {
        super(false);
        this.setAttribute(entries);
    }

}

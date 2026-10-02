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

import java.util.*;
import java.util.function.Supplier;

class EmptyAttributes implements Attributes {

    public EmptyAttributes() {
        super();
    }

    @Override
    public <T> T getAttribute(AttrKey<? extends T> key) {
        return null;
    }

    @Override
    public <T> T getAttribute(AttrKey<? extends T> key, T defaultValue) {
        return defaultValue;
    }

    @Override
    public <T> T computeIfAbsent(AttrKey<? extends T> key, T value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T computeIfAbsent(AttrKey<? extends T> key, Supplier<T> supplier) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T setIfAbsent(AttrKey<? extends T> key, T value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T removeAttribute(AttrKey<? extends T> key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> void setAttribute(AttrKey<? extends T> key, T value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAttribute(Map<AttrKey<?>, ?> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAttribute(AttrEntry<?> entry) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAttribute(Collection<AttrEntry<?>> entries) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void setAttribute(AttrEntry<?>... entries) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void removeAttribute(Collection<AttrKey<?>> keys) {
        throw new UnsupportedOperationException();

    }

    @Override
    public Map<AttrKey<?>, Object> getAttributeMap() {
        return Collections.emptyMap();
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public void clearAttribute() {
        throw new UnsupportedOperationException();
    }

}

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

package com.tny.game.common.enums;

import com.tny.game.common.concurrent.collection.*;

import java.util.Map;
import java.util.function.Function;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * 标识
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/6 19:48
 **/
public class EnumerableSymbol<E extends Enumerable<?>, T> {

    private static final Map<String, EnumerableSymbol<?, ?>> symbolMap = new CopyOnWriteMap<>();

    private final Class<E> enumClass;

    private final String symbol;

    private final Function<E, T> valueGetter;

    public static <E extends Enumerable<?>, T> EnumerableSymbol<E, T> symbolOf(Class<E> enumClass, String symbol, Function<E, T> valueGetter) {
        return as(symbolMap.computeIfAbsent(enumClass.getName() + "." + symbol, (k) -> new EnumerableSymbol<>(enumClass, symbol, valueGetter)));
    }

    private EnumerableSymbol(Class<E> enumClass, String symbol, Function<E, T> valueGetter) {
        this.enumClass = enumClass;
        this.symbol = symbol;
        this.valueGetter = valueGetter;
    }

    public Class<E> getEnumClass() {
        return enumClass;
    }

    public String getSymbol() {
        return symbol;
    }

    public T getSymbolValue(E item) {
        return valueGetter.apply(item);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("EnumerableSymbol{");
        sb.append("symbol='").append(symbol).append('\'');
        sb.append('}');
        return sb.toString();
    }

}

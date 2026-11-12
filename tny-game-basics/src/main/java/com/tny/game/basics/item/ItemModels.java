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

package com.tny.game.basics.item;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Kun Yang on 2017/7/18.
 */
public class ItemModels {

    private static Map<Integer, String> modelDesc = new ConcurrentHashMap<>();

    private static Map<Integer, String> modelAlias = new ConcurrentHashMap<>();

    public static void register(Model model) {
        modelDesc.put(model.getId(), model.getDesc());
        modelAlias.put(model.getId(), model.getAlias());
    }

    public static String name(int id) {
        String value = modelDesc.get(id);
        if (value != null) {
            return value;
        }
        return String.valueOf(id);
    }

    public static String alias(int id) {
        String value = modelAlias.get(id);
        if (value != null) {
            return value;
        }
        return String.valueOf(id);
    }

}

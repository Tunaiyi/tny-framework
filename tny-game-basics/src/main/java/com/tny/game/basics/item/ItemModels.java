/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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

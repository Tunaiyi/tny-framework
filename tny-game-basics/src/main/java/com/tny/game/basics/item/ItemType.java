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

import com.tny.game.common.enums.*;

/**
 * 事物类型接口
 *
 * @author KGTny
 */
public interface ItemType extends IntEnumerable {

    int ID_TAIL_SIZE = 1000000;

    /**
     * 获取别名头
     */
    String getAliasHead();

    /**
     * @return 描述 秒速
     */
    String getDesc();

    /**
     * @return id前缀
     */
    default int getIdHead() {
        return getId() / ID_TAIL_SIZE;
    }

    /**
     * 创建 itemId
     *
     * @param index 索引
     * @return 返回 itemID
     */
    default long itemIdOf(int index) {
        return Long.parseLong(this.getIdHead() + "" + index);
    }

    /**
     * 创建 itemId
     *
     * @param index 索引
     * @return 返回 itemID
     */
    default long itemIdOf(long index) {
        return Long.parseLong(this.getIdHead() + "" + index);
    }

    /**
     * 创建 item别名
     *
     * @param alisa 别名
     * @return 返回创建别名
     */
    default String alisaOf(String alisa) {
        return getAliasHead() + "$" + alisa;
    }

}
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

package com.tny.game.basics.upgrade;

/**
 * 通过经验升级的 item
 * Created by Kun Yang on 2017/4/5.
 */
public interface ExpUpgradableItem<IM extends ExpUpgradableItemModel> extends UpgradableItem<IM> {

    /**
     * @return 获得升级经验
     */
    long getLevelExp();

    /**
     * @return 获取升级经验类型
     */
    default ExpType getLevelExpType() {
        return this.getModel().getLevelExpType();
    }

    /**
     * @return 获取升级最大经验
     */
    default long getMaxLevelExp() {
        return this.getModel().getMaxLevelExp(this);
    }

    /**
     * @return 升级经验是否已满
     */
    default boolean isLevelExpFull() {
        return this.getLevelExp() >= this.getMaxLevelExp();
    }

    /**
     * @return 获取升级经验百分比
     */
    default float getLevelExpPct() {
        return (float) ((double) this.getLevelExp() / this.getMaxLevelExp());
    }

}

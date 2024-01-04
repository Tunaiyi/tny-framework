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

package com.tny.game.basics.upgrade;

/**
 * 通过经验升级的 item Model
 * Created by Kun Yang on 2017/4/5.
 */
public interface ExpUpgradableItemModel extends UpgradableItemModel {

    /**
     * @return 获取升级经验类型
     */
    ExpType getLevelExpType();

    /**
     * @param item 相关Item
     * @return 获取Item的最大升级经验
     */
    long getMaxLevelExp(ExpUpgradableItem<? extends ExpUpgradableItemModel> item, Object... attributes);

}

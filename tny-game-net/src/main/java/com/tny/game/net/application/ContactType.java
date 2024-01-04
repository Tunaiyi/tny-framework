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

package com.tny.game.net.application;

import com.tny.game.common.enums.*;

/**
 * 通讯者类型
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/21 04:20
 **/
public interface ContactType extends IntEnumerable {

    String DEFAULT_USER_TYPE = "_user";

    String ANONYMITY_USER_TYPE = "_anonymity";

    String getGroup();

}

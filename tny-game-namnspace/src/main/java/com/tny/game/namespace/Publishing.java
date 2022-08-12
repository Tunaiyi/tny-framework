/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.namespace;

import com.tny.game.codec.*;

import java.util.concurrent.CompletableFuture;

/**
 * 发布流程
 * <p>
 *
 * @author kgtny
 * @date 2022/7/9 04:52
 **/
public interface Publishing<T> {

    CompletableFuture<NameNode<T>> doPublish(NamespaceExplorer explorer, String path, T value, ObjectMineType<T> mineType, Lessee lessee);

}

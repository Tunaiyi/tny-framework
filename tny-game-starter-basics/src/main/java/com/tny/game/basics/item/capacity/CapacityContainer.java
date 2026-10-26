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

package com.tny.game.basics.item.capacity;

/**
 * 可通过能力接口
 * Created by Kun Yang on 16/3/12.
 */
public interface CapacityContainer extends CapacitySupply {

    /**
     * 刷新
     *
     * @param supplier 持有的提供器
     */
    void refresh(CapacitySupplier supplier);

    /**
     * 失效
     *
     * @param supplier 持有的提供器
     */
    void invalid(CapacitySupplier supplier);

    /**
     * 生效
     *
     * @param supplier 持有的提供器
     */
    void effect(CapacitySupplier supplier);

}

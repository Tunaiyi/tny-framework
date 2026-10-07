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

/**
 * @author KGTny
 * @ClassName: Attributes
 * @Description: 属性对象接口
 * @date 2011-9-21 ����10:48:40
 * <p>
 * 属性对象接口
 * <p>
 * <br>
 */
public class AttributeHolder {

    private transient volatile Attributes attributes;

    private final Object holderLock = new Object();

    public Attributes attributes() {
        if (this.attributes != null) {
            return this.attributes;
        }
        synchronized (this.holderLock) {
            if (this.attributes != null) {
                return this.attributes;
            }
            return this.attributes = ContextAttributes.create();
        }
    }

}

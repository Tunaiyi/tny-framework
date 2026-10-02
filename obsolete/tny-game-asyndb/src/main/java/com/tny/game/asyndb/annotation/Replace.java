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
package com.tny.game.asyndb.annotation;

import java.lang.annotation.*;

/**
 * 强制替换注解
 * 如果更新的对象不是同一个时是否替换
 * replaceObject = true :
 * 更新对象直接替换内存对象
 * replaceObject = false
 * 更新对象不是内存对象则刨出异常
 *
 * @author KGTny
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Replace {

}

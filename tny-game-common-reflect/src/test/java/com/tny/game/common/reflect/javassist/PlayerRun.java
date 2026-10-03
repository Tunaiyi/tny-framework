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

package com.tny.game.common.reflect.javassist;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/21 11:36
 **/
public class PlayerRun implements MethodInvoker {

    @Override
    public Object invoke(Object host, Object... args) {
        return ((JavassistAccessorsTest.Player) host).getAge();
    }

}

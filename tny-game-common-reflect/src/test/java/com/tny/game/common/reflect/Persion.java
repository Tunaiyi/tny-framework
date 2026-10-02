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

package com.tny.game.common.reflect;

@SuppressWarnings("unused")
public class Persion {

    /**
     * @uml.property name="name"
     */
    private String name;

    /**
     * @uml.property name="age"
     */
    @Owner
    private String age;

    /**
     * @uml.property name="sex"
     */
    @Owner
    private boolean sex;

    /**
     * @return
     * @uml.property name="name"
     */
    @Pepole
    public String getName() {
        return name;
    }

    /**
     * @param name
     * @uml.property name="name"
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return
     * @uml.property name="age"
     */
    @Pepole
    protected String getAge() {
        return age;
    }

    /**
     * @param age
     * @uml.property name="age"
     */
    protected void setAge(String age) {
        this.age = age;
    }

    /**
     * @return
     * @uml.property name="sex"
     */
    @Pepole
    private boolean isSex() {
        return sex;
    }

    /**
     * @param sex
     * @uml.property name="sex"
     */
    private void setSex(boolean sex) {
        this.sex = sex;
    }

}

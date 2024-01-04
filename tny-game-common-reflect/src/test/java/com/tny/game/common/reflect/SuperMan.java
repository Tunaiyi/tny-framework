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

package com.tny.game.common.reflect;

@SuppressWarnings("unused")
public class SuperMan extends Persion {

    /**
     * @uml.property name="superName"
     */
    @Owner
    private String superName;

    /**
     * @uml.property name="superAge"
     */
    @Owner
    private String superAge;

    /**
     * @uml.property name="superSex"
     */
    private boolean superSex;

    /**
     * @return
     * @uml.property name="superName"
     */
    @Pepole
    public String getSuperName() {
        return superName;
    }

    /**
     * @param superName
     * @uml.property name="superName"
     */
    public void setSuperName(String superName) {
        this.superName = superName;
    }

    /**
     * @return
     * @uml.property name="superAge"
     */
    @Pepole
    protected String getSuperAge() {
        return superAge;
    }

    /**
     * @param superAge
     * @uml.property name="superAge"
     */
    protected void setSuperAge(String superAge) {
        this.superAge = superAge;
    }

    /**
     * @return
     * @uml.property name="superSex"
     */
    @Pepole
    private boolean isSuperSex() {
        return superSex;
    }

    /**
     * @param superSex
     * @uml.property name="superSex"
     */
    private void setSuperSex(boolean superSex) {
        this.superSex = superSex;
    }

}

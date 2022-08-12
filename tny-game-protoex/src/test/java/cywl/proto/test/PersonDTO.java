/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package cywl.proto.test;

import com.tny.game.protoex.annotations.*;
import com.tny.game.protoex.field.*;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

@ProtoEx(100)
public class PersonDTO {

    //	@Tag(1)
    @ProtoExField(1)
    private String name;

    //	@Tag(2)
    @ProtoExField(value = 2, conf = @ProtoExConf(format = FieldFormat.ZigZag))
    private int age;

    //	@Tag(3)
    @ProtoExField(value = 3, conf = @ProtoExConf(format = FieldFormat.ZigZag))
    private List<Integer> friendIDList = new ArrayList<Integer>();

    @ProtoExField(4)
    private Goods equip;

    @ProtoExField(5)
    private List<Goods> goodsList = new ArrayList<Goods>();

    @ProtoExField(6)
    private float floatTest = Float.MAX_VALUE;

    @ProtoExField(7)
    private double doubleTest = Double.MAX_VALUE;

    @ProtoExField(8)
    private boolean trueTest = true;

    @ProtoExField(9)
    private boolean falseTest = false;

    @ProtoExField(10)
    private List<String> noteList = new ArrayList<String>();

    @ProtoExField(11)
    private byte[] paramBytes = new byte[]{1, 2, 3, 4, 5};

    public PersonDTO() {
    }

    public PersonDTO(String name, int age, Goods goods, List<Integer> friendIDList) {
        this.name = name;
        this.age = age;
        this.friendIDList = friendIDList;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return this.age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<Integer> getFriendIDList() {
        return this.friendIDList;
    }

    public void setFriendIDList(List<Integer> friendIDList) {
        this.friendIDList = friendIDList;
    }

    public Goods getEquip() {
        return this.equip;
    }

    public void setEquip(Goods equip) {
        this.equip = equip;
    }

    public List<Goods> getGoodsList() {
        return this.goodsList;
    }

    public void setGoodsList(List<Goods> goodsList) {
        this.goodsList = goodsList;
    }

    public float getFloatTest() {
        return this.floatTest;
    }

    public void setFloatTest(float floatTest) {
        this.floatTest = floatTest;
    }

    public double getDoubleTest() {
        return this.doubleTest;
    }

    public void setDoubleTest(double doubleTest) {
        this.doubleTest = doubleTest;
    }

    public boolean isTrueTest() {
        return this.trueTest;
    }

    public void setTrueTest(boolean trueTest) {
        this.trueTest = trueTest;
    }

    public boolean isFalseTest() {
        return this.falseTest;
    }

    public void setFalseTest(boolean falseTest) {
        this.falseTest = falseTest;
    }

    public List<String> getNoteList() {
        return this.noteList;
    }

    public void setNoteList(List<String> noteList) {
        this.noteList = noteList;
    }

    public byte[] getParamBytes() {
        return this.paramBytes;
    }

    public void setParamBytes(byte[] paramBytes) {
        this.paramBytes = paramBytes;
    }

    public void addFriend(Integer friendID) {
        this.friendIDList.add(friendID);
    }

    public void addFriend(Collection<Integer> friendID) {
        this.friendIDList.addAll(friendID);
    }

    public void addGoods(Goods goods) {
        this.goodsList.add(goods);
    }

    public void addGoods(Collection<Goods> goodsCollection) {
        this.goodsList.addAll(goodsCollection);
    }

    @Override
    public String toString() {
        return "Person [name=" + this.name + ", age=" + this.age + ", friend=" + ArrayUtils.toString(this.friendIDList) + ", equip=" + this.equip +
                ", goodsList=" + ArrayUtils.toString(this.goodsList) + ", noteList="
                + ArrayUtils.toString(this.noteList) + ", floatTest=" + this.floatTest + ", doubleTest=" + this.doubleTest + ", trueTest=" +
                this.trueTest + ", falseTest=" + this.falseTest + ", paramBytes="
                + Arrays.toString(this.paramBytes) + "]";
    }

    //	", friend=" + ArrayUtils.toString(friendIDList) + ", equip=" + equip + ", goodsList=" + ArrayUtils.toString(goodsList) +

}

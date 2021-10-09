package com.tny.game.basics.item;

import java.util.*;

@SuppressWarnings({"unchecked", "rawtypes"})
public class OwnerTest {

    List<Test> testList = new ArrayList<>();

    public void doSay(Object number) {
        for (Test test : this.testList) {
            test.say(number);
        }
    }

    public static void main(String[] args) {
        OwnerTest ownerTest = new OwnerTest();
        ownerTest.testList.add(new Test<>(1000L));
        ownerTest.testList.add(new Test<>(1000));
        ownerTest.testList.add(new Test<>("1000"));

        ownerTest.doSay(100);
    }

}

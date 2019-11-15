package com.tny.game.base.item;

import java.util.*;

@SuppressWarnings({"unchecked", "rawtypes"})
public class OwnerTest {

    List<Test> testList = new ArrayList<Test>();

    public void doSay(Object number) {
        for (Test test : testList) {
            test.say(number);
        }
    }

    public static void main(String[] args) {
        OwnerTest ownerTest = new OwnerTest();
        ownerTest.testList.add(new Test<Long>(1000L));
        ownerTest.testList.add(new Test<Integer>(1000));
        ownerTest.testList.add(new Test<String>("1000"));

        ownerTest.doSay(100);
    }

}

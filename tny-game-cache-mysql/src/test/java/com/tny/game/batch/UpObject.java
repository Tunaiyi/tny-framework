package com.tny.game.batch;

import java.util.concurrent.ThreadLocalRandom;

public class UpObject {

    public int id;

    public String name;

    public int age;

    public int gender;

    public UpObject() {
    }

    public UpObject(int id) {
        this.id = id;
        this.name = "one" + id;
        this.age = ThreadLocalRandom.current().nextInt(10) + 10;
        this.gender = ThreadLocalRandom.current().nextInt(1);
    }

    public void reset() {
        if (this.name.startsWith("one"))
            this.name = "tow" + this.id;
        else
            this.name = "one" + this.id;
        this.age = ThreadLocalRandom.current().nextInt(10) + 10;
        this.gender = ThreadLocalRandom.current().nextInt(1);
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getGender() {
        return this.gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

}

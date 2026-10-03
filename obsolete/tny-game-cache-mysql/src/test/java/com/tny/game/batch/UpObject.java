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
        if (this.name.startsWith("one")) {
            this.name = "tow" + this.id;
        } else {
            this.name = "one" + this.id;
        }
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

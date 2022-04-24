package com.tny.game.cache.testclass;

import java.io.Serializable;

public class Person implements IPerson, Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private int id;

    private String name;

    private int age;

    private int sex;

    public Person() {
    }

    public Person(int id, String name, int age, int sex) {
        super();
        this.id = id;
        this.name = name;
        this.age = age;
        this.sex = sex;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + age;
        result = prime * result + id;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + sex;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Person other = (Person)obj;
        if (age != other.age) {
            return false;
        }
        if (id != other.id) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (sex != other.sex) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "PersonDao [id=" + id + ", name=" + name + ", age=" + age
                + ", sex=" + sex + "]";
    }

}

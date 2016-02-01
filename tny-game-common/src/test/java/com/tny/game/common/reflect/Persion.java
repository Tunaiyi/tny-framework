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

package com.tny.game.cglib;

public class Player {

    private String name;

    public Player() {
        super();
    }

    public Player(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Save(name = "@Save Tom eat")
    public void eat() {
        //		System.out.println(this.name + " eat!");
    }

    @Save(name = "@Save Tom walk")
    public void walk() {
        //		System.out.println(this.name + " walk!");
    }

}

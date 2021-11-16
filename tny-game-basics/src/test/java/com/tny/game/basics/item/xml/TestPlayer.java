package com.tny.game.basics.item.xml;

import com.tny.game.basics.item.*;

public class TestPlayer extends AbstractItem<ItemModel> {

    private int number;
    private int level;
    private String itemAlias;

    public TestPlayer(int number, int level, String alias) {
        super();
        this.number = number;
        this.level = level;
        this.itemAlias = alias;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setItemAlias(String itemAlias) {
        this.itemAlias = itemAlias;
    }

    public int getNumber() {
        return number;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public long getId() {
        return 0;
    }

    @Override
    public String getAlias() {
        return itemAlias;
    }

}
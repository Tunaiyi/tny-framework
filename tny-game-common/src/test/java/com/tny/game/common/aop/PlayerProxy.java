package com.tny.game.common.aop;

import com.tny.game.common.reflect.aop.annotation.*;


public class PlayerProxy extends Player {

    @Override
    @AOP
    public Player friend(int data, Player player, long value) {
        System.out.println("PlayerProxy.friend");
        super.friend(data, player, value);
        return null;
    }

}

package com.tny.game.actor;

/**
 * 决策者
 * Created by Kun Yang on 16/1/17.
 */
public interface Decider {

    Directive decide(Throwable cause, Directive defDirective);

}

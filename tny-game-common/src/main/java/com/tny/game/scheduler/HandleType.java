package com.tny.game.scheduler;

public enum HandleType {

    /**
     * 同一个任务累计到n次在同一时间触发执行时,执行任务n次
     */
    ALWAYS,

    /**
     * 同一个任务累计到n次在同一时间触发执行时,执行任务1次
     */
    ONCE;

}

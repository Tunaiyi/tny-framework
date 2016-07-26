package com.tny.game.net.initer;

/**
 * 启动后初始化器
 * Created by Kun Yang on 16/7/24.
 */
public final class PerIniter extends StartIniter<PerIniter> {

    public static PerIniter initer(Class<? extends ServerPreStart> clazz) {
        return initer(clazz, InitLevel.LEVEL_5);
    }

    public static PerIniter initer(Class<? extends ServerPreStart> clazz, InitLevel initLevel) {
        PerIniter initer = getIniter(clazz);
        if (initer == null) {
            initer = new PerIniter(clazz, initLevel);
            putIniter(initer);
        }
        return initer;
    }

    private PerIniter(Class<?> initerClass, InitLevel initLevel) {
        super(initerClass, initLevel);
    }

}

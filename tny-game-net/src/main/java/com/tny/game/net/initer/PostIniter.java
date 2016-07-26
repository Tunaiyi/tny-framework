package com.tny.game.net.initer;

/**
 * 启动后初始化器
 * Created by Kun Yang on 16/7/24.
 */
public final class PostIniter extends StartIniter<PostIniter> {

    public static PostIniter initer(Class<? extends ServerPostStart> clazz) {
        return initer(clazz, InitLevel.LEVEL_5);
    }

    public static PostIniter initer(Class<? extends ServerPostStart> clazz, InitLevel initLevel) {
        PostIniter initer = getIniter(clazz);
        if (initer == null) {
            initer = new PostIniter(clazz, initLevel);
            putIniter(initer);
        }
        return initer;
    }

    private PostIniter(Class<?> initerClass, InitLevel initLevel) {
        super(initerClass, initLevel);
    }

}

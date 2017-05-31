package com.tny.game.actor.stage;

/**
 * 当fragment完成(无论成功或失败)时阶段完成
 * Created by Kun Yang on 16/1/22.
 */
class ThenDoneStage<R> extends DefaultStage<R> {

    public ThenDoneStage(Object name, Fragment<?, R> fragment) {
        super(name, fragment);
    }

    @Override
    public boolean doCheck(Fragment prev) {
        return prev.isDone();
    }

}

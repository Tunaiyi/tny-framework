package com.tny.game.actor;


import com.tny.game.actor.stage.Stageable;
import com.tny.game.actor.stage.Stages;
import com.tny.game.actor.stage.VoidTaskStage;

/**
 * 响应的未来对象
 *
 * @author KGTny
 */
public interface VoidAnswer extends Answer<Void>, Stageable<VoidTaskStage> {

    /**
     * @param listener 添加未来响应监听器
     */
    void addListener(VoidAnswerListener listener);

    /**
     * @return 转换成可执行阶段
     */
    @Override
    default VoidTaskStage stage() {
        return Stages.waitUntil(this::isDone);
    }

//    @Override
//    default Stageable<VoidTaskStage> stageable() {
//        return this;
//    }

}

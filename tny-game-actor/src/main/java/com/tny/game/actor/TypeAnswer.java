package com.tny.game.actor;


import com.tny.game.actor.stage.Stageable;
import com.tny.game.actor.stage.Stages;
import com.tny.game.actor.stage.TypeTaskStage;
import com.tny.game.common.utils.Done;

/**
 * 响应的未来对象
 *
 * @param <V>
 * @author KGTny
 */
public interface TypeAnswer<V> extends Answer<V>, Stageable<TypeTaskStage<V>> {

    /**
     * @param listener 添加未来响应监听器
     */
    void addListener(AnswerListener<V> listener);

    /**
     * @return 转换成可执行阶段
     */
    @Override
    default TypeTaskStage<V> stage() {
        return Stages.waitFor(this::result);
    }

//    @Override
//    default Stageable<TypeTaskStage<V>> stageable() {
//        return this;
//    }

    /**
     * @return 获取结果 非阻塞, Done为true代表已经完成, 但不代表成功
     */
    Done<V> result();

}

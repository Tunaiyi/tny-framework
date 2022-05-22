package com.tny.game.net.message;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.base.*;

/**
 * 消息者工厂
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/21 04:19
 **/
@UnitInterface
public interface MessagerFactory {

    /**
     * 创建 Messager
     *
     * @param type       消息者类型
     * @param messagerId 消息者id
     * @return 返回创建的 messager
     */
    <M extends Messager> M createMessager(MessagerType type, long messagerId);

    /**
     * 创建 Messager
     *
     * @param messager 转发消息者
     * @return 返回创建的 messager
     */
    <M extends Messager> M createMessager(ForwardMessager messager);

}

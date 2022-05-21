package com.tny.game.net.message;

import com.tny.game.net.base.*;

import java.util.Set;

/**
 * 消息者工厂
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/21 04:19
 **/
public interface MessagerFactory<M extends Messager> {

    /**
     * @return 创建的Messager类型
     */
    Set<MessagerType> getMessagerTypes();

    /**
     * 创建 Messager
     *
     * @param type       消息者类型
     * @param messagerId 消息者id
     * @return 返回创建的 messager
     */
    M createMessager(MessagerType type, long messagerId);

}

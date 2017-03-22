package com.tny.game.net.client.rmi;

import com.tny.game.net.message.Message;

import java.rmi.RemoteException;

/**
 * 远程RMI接口
 *
 * @author Kun.y
 */
public interface RMIService {

    /**
     * 发送请求
     *
     * @param message 消息
     * @return 返回相应
     */
    <FID, TID> Message send(Message<FID> message) throws RemoteException;

}
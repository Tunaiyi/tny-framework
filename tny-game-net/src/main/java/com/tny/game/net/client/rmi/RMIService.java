package com.tny.game.net.client.rmi;

import com.tny.game.net.dispatcher.Request;
import com.tny.game.net.dispatcher.Response;

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
     * @param request 请求对象
     * @return 返回相应
     */
    public Response send(Request request) throws RemoteException;

}
/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.test;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2019-11-20 21:59
 */
public class Sender {

    private static final PID<MessageA> GET_EQUIP = PID.create(10001);

    private static final PID<MessageB> GET_GOLD = PID.create(10002);

    private static final PID<MessageB> GET_GOLD_1 = PID.create(10002);

    public <M> void send(PID<M> pid, M message) {
        int pidNum = pid.getPid();
        // doSend(pidNum, message);
    }

    public static void main(String[] args) {
        Sender sender = new Sender();
        sender.send(Sender.GET_EQUIP, new MessageA());
        sender.send(Sender.GET_GOLD, new MessageB());
    }

}

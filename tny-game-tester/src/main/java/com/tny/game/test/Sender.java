/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

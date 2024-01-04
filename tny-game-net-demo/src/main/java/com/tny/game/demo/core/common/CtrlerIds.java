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

package com.tny.game.demo.core.common;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2018-10-31 16:51
 */
public interface CtrlerIds {

    int LOGIN = 100;
    int LOGIN$LOGIN = 100_01;

    int SPEAK = 200;
    int SPEAK$SAY = 200_02;
    int SPEAK$PUSH = 200_03;
    int SPEAK$PING = 200_04;
    int SPEAK$DELAY_SAY = 200_05;
    int SPEAK$TEST = 200_06;

    int SPEAK$SAY_FOR_RPC = 200_07;
    int SPEAK$SAY_FOR_CONTENT = 200_08;

    int PLAYER = 201;
    int PLAYER$GET = 201_00;
    int PLAYER$ADD = 201_01;
    int PLAYER$SAVE = 201_02;
    int PLAYER$UPDATE = 201_03;
    int PLAYER$DELETE = 201_04;

    //	int GAME_LOGIN = 1000;
    //	int GAME_LOGIN$LOGIN = 1000_01;

}

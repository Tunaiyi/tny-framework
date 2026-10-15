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

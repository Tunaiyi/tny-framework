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

package com.tny.game.demo.core.server.controller;

import com.tny.game.common.lifecycle.*;
import com.tny.game.common.result.*;
import com.tny.game.data.*;
import com.tny.game.demo.core.common.*;
import com.tny.game.demo.core.common.dto.*;
import com.tny.game.demo.core.server.*;
import com.tny.game.net.annotation.*;
import com.tny.game.net.application.*;
import com.tny.game.net.netty4.configuration.command.*;
import org.slf4j.*;

import static com.tny.game.net.application.ContactType.*;
import static com.tny.game.net.message.MessageMode.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2018-10-31 16:46
 */
@AuthenticationRequired(DEFAULT_USER_TYPE)
@BeforePlugin(SpringBootParamFilterPlugin.class)
@RpcController(modes = {REQUEST, PUSH})
public class PlayerController implements AppPostStart {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerController.class);

    private final EntityCacheManager<Long, DemoPlayer> entityCacheManager;

    public PlayerController(EntityCacheManager<Long, DemoPlayer> entityCacheManager) {
        this.entityCacheManager = entityCacheManager;
    }

    @RpcRequest(CtrlerIds.PLAYER$GET)
    public RpcResult<PlayerDTO> getPlayer(@RpcParam long playerId) {
        DemoPlayer player = entityCacheManager.getEntity(playerId);
        if (player != null) {
            return RpcResults.success(new PlayerDTO(player));
        } else {
            LOGGER.error("get Player : 玩家 {} 不存在", playerId);
            return RpcResults.fail(ResultCode.FAILURE);
        }
    }

    @RpcRequest(CtrlerIds.PLAYER$ADD)
    public RpcResult<PlayerDTO> addPlayer(@RpcParam long playerId, @RpcParam String name, @RpcParam int age) {
        entityCacheManager.getEntity(playerId);
        DemoPlayer player = new DemoPlayer(playerId, name, age);
        if (entityCacheManager.insertEntity(player)) {
            return RpcResults.success(new PlayerDTO(player));
        } else {
            LOGGER.error("add Player : 添加玩家 {} 失败", player);
            return RpcResults.fail(ResultCode.FAILURE);
        }
    }

    @RpcRequest(CtrlerIds.PLAYER$UPDATE)
    public RpcResult<PlayerDTO> updatePlayer(@RpcParam long playerId, @RpcParam String name, @RpcParam int age) {
        DemoPlayer player = entityCacheManager.getEntity(playerId);
        if (player != null) {
            player.setAge(age);
            player.setName(name);
            if (entityCacheManager.updateEntity(player)) {
                return RpcResults.success(new PlayerDTO(player));
            }
            LOGGER.error("update Player : 更新玩家 {} 失败", player);
        } else {
            LOGGER.error("update Player : 玩家 {} 不存在", playerId);
        }
        return RpcResults.fail(ResultCode.FAILURE);
    }

    @RpcRequest(CtrlerIds.PLAYER$SAVE)
    public RpcResult<PlayerDTO> savePlayer(@RpcParam long playerId, @RpcParam String name, @RpcParam int age) {
        DemoPlayer player = null;
        for (int id = 0; id < 500000; id++) {
            player = new DemoPlayer(playerId + id, name, age);
            if (!entityCacheManager.saveEntity(player)) {
                LOGGER.error("save Player : 保存玩家 {} 失败", player);
            }
        }
        return RpcResults.success(new PlayerDTO(player));
    }

    @RpcRequest(CtrlerIds.PLAYER$DELETE)
    public RpcResult<PlayerDTO> deletePlayer(@RpcParam long playerId) {
        DemoPlayer player = entityCacheManager.getEntity(playerId);
        if (player != null) {
            if (entityCacheManager.deleteEntity(player)) {
                return RpcResults.success(new PlayerDTO(player));
            }
            LOGGER.error("delete Player :  删除玩家 {} 失败", player);
        } else {
            LOGGER.error("delete Player : 玩家 {} 不存在", playerId);
        }
        return RpcResults.fail(ResultCode.FAILURE);
    }

    @Override
    public void postStart() {
        entityCacheManager.getEntity(1L);
    }

}

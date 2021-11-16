package com.tny.game.demo.core.server.controller;

import com.tny.game.common.lifecycle.*;
import com.tny.game.common.result.*;
import com.tny.game.data.*;
import com.tny.game.demo.core.common.*;
import com.tny.game.demo.core.common.dto.*;
import com.tny.game.demo.core.server.*;
import com.tny.game.net.annotation.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.*;
import com.tny.game.net.netty4.configuration.command.*;
import org.slf4j.*;

import javax.annotation.Resource;

import static com.tny.game.net.message.MessageMode.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-31 16:46
 */
@AuthenticationRequired(Certificates.DEFAULT_USER_TYPE)
@BeforePlugin(SpringBootParamFilterPlugin.class)
@RpcController(modes = {REQUEST, PUSH})
public class PlayerController implements AppPostStart {

	private static final Logger LOGGER = LoggerFactory.getLogger(PlayerController.class);

	@Resource
	private EntityCacheManager<Long, DemoPlayer> entityCacheManager;

	@Rpc(CtrlerIDs.PLAYER$GET)
	public RpcResult<PlayerDTO> getPlayer(@MsgParam long playerId) {
		DemoPlayer player = entityCacheManager.getEntity(playerId);
		if (player != null) {
			return RpcResults.success(new PlayerDTO(player));
		} else {
			LOGGER.error("get Player : 玩家 {} 不存在", playerId);
			return RpcResults.fail(ResultCode.FAILURE);
		}
	}

	@Rpc(CtrlerIDs.PLAYER$ADD)
	public RpcResult<PlayerDTO> addPlayer(@MsgParam long playerId, @MsgParam String name, @MsgParam int age) {
		entityCacheManager.getEntity(playerId);
		DemoPlayer player = new DemoPlayer(playerId, name, age);
		if (entityCacheManager.insertEntity(player)) {
			return RpcResults.success(new PlayerDTO(player));
		} else {
			LOGGER.error("add Player : 添加玩家 {} 失败", player);
			return RpcResults.fail(ResultCode.FAILURE);
		}
	}

	@Rpc(CtrlerIDs.PLAYER$UPDATE)
	public RpcResult<PlayerDTO> updatePlayer(@MsgParam long playerId, @MsgParam String name, @MsgParam int age) {
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

	@Rpc(CtrlerIDs.PLAYER$SAVE)
	public RpcResult<PlayerDTO> savePlayer(@MsgParam long playerId, @MsgParam String name, @MsgParam int age) {
		DemoPlayer player = null;
		for (int id = 0; id < 500000; id++) {
			player = new DemoPlayer(playerId + id, name, age);
			if (!entityCacheManager.saveEntity(player)) {
				LOGGER.error("save Player : 保存玩家 {} 失败", player);
			}
		}
		return RpcResults.success(new PlayerDTO(player));
	}

	@Rpc(CtrlerIDs.PLAYER$DELETE)
	public RpcResult<PlayerDTO> deletePlayer(@MsgParam long playerId) {
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
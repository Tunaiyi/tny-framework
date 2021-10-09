package com.tny.game.demo.core.server.controller;

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
@Controller(CtrlerIDs.PLAYER)
@AuthenticationRequired(Certificates.DEFAULT_USER_TYPE)
@BeforePlugin(SpringBootParamFilterPlugin.class)
@MessageFilter(modes = {REQUEST, PUSH})
public class PlayerController {

	private static final Logger LOGGER = LoggerFactory.getLogger(PlayerController.class);

	@Resource
	private EntityCacheManager<Long, DemoPlayer> entityCacheManager;

	@Controller(CtrlerIDs.PLAYER$GET)
	public CommandResult getPlayer(@MsgParam long playerId) {
		DemoPlayer player = entityCacheManager.getEntity(playerId);
		if (player != null) {
			return CommandResults.success(new PlayerDTO(player));
		} else {
			LOGGER.error("get Player : 玩家 {} 不存在", playerId);
			return CommandResults.fail(ResultCode.FAILURE);
		}
	}

	@Controller(CtrlerIDs.PLAYER$ADD)
	public CommandResult addPlayer(@MsgParam long playerId, @MsgParam String name, @MsgParam int age) {
		entityCacheManager.getEntity(playerId);
		DemoPlayer player = new DemoPlayer(playerId, name, age);
		if (entityCacheManager.insertEntity(player)) {
			return CommandResults.success(new PlayerDTO(player));
		} else {
			LOGGER.error("add Player : 添加玩家 {} 失败", player);
			return CommandResults.fail(ResultCode.FAILURE);
		}
	}

	@Controller(CtrlerIDs.PLAYER$UPDATE)
	public CommandResult updatePlayer(@MsgParam long playerId, @MsgParam String name, @MsgParam int age) {
		DemoPlayer player = entityCacheManager.getEntity(playerId);
		if (player != null) {
			player.setAge(age);
			player.setName(name);
			if (entityCacheManager.updateEntity(player)) {
				return CommandResults.success(new PlayerDTO(player));
			}
			LOGGER.error("update Player : 更新玩家 {} 失败", player);
		} else {
			LOGGER.error("update Player : 玩家 {} 不存在", playerId);
		}
		return CommandResults.fail(ResultCode.FAILURE);
	}

	@Controller(CtrlerIDs.PLAYER$SAVE)
	public CommandResult savePlayer(@MsgParam long playerId, @MsgParam String name, @MsgParam int age) {
		DemoPlayer player = new DemoPlayer(playerId, name, age);
		if (entityCacheManager.saveEntity(player)) {
			return CommandResults.success(new PlayerDTO(player));
		} else {
			LOGGER.error("save Player : 保存玩家 {} 失败", player);
		}
		return CommandResults.fail(ResultCode.FAILURE);
	}

	@Controller(CtrlerIDs.PLAYER$DELETE)
	public CommandResult deletePlayer(@MsgParam long playerId) {
		DemoPlayer player = entityCacheManager.getEntity(playerId);
		if (player != null) {
			if (entityCacheManager.deleteEntity(player)) {
				return CommandResults.success(new PlayerDTO(player));
			}
			LOGGER.error("delete Player :  删除玩家 {} 失败", player);
		} else {
			LOGGER.error("delete Player : 玩家 {} 不存在", playerId);
		}
		return CommandResults.fail(ResultCode.FAILURE);
	}

}

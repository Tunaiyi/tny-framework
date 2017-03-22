package com.tny.game.suite.scheduler;

import com.tny.game.net.command.CommandResult;
import com.tny.game.net.plugin.ControllerPlugin;
import com.tny.game.net.plugin.PluginContext;
import com.tny.game.net.session.Session;
import com.tny.game.suite.login.IDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.tny.game.suite.SuiteProfiles.*;

@Component
@Profile({SCHEDULER, GAME})
public class TaskReceiverSchedulerPlugin implements ControllerPlugin {

    public static final Logger LOGGER = LoggerFactory.getLogger(TaskReceiverSchedulerPlugin.class);

    public static final Logger TEST_LOGGER = LoggerFactory.getLogger("test");

    @Resource
    private TimeTaskSchedulerService taskSchedulerService;

    @Override
    public CommandResult execute(Request request, CommandResult result, PluginContext context) throws Exception {
        if (request.getUserGroup().equals(Session.DEFAULT_USER_GROUP)) {
            if (IDUtils.isSystem(request.getUserID())) {
                Session session = request.getSession();
                TEST_LOGGER.error("{} 非玩家ID | 登陆 {} | session {} | 请求 {} 协议", request.getUserID(), request.isLogin(), session == null ? null : session.getUID(), request.getProtocol(), new RuntimeException());
            } else {
                try {
                    this.taskSchedulerService.checkPlayerTask(request.getUserID(), ReceiverType.PLAYER);
                } catch (Exception e) {
                    LOGGER.error("", e);
                }
            }
        }
        return context.passToNext(request, result);
    }

}

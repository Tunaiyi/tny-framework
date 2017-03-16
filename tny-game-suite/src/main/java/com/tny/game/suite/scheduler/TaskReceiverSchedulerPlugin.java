package com.tny.game.suite.scheduler;

import com.tny.game.net.dispatcher.CommandResult;
import com.tny.game.net.dispatcher.Request;
import com.tny.game.net.session.Session;
import com.tny.game.net.dispatcher.plugin.ControllerPlugin;
import com.tny.game.net.dispatcher.plugin.PluginContext;
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

    @Resource
    private TimeTaskSchedulerService taskSchedulerService;

    @Override
    public CommandResult execute(Request request, CommandResult result, PluginContext context) throws Exception {
        if (request.getUserGroup().equals(Session.DEFAULT_USER_GROUP)) {
            try {
                this.taskSchedulerService.checkPlayerTask(request.getUserID(), ReceiverType.PLAYER);
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
        return context.passToNext(request, result);
    }

}

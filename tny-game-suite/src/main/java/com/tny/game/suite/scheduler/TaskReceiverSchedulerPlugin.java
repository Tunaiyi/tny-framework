package com.tny.game.suite.scheduler;

import com.tny.game.net.command.ControllerPlugin;
import com.tny.game.net.command.InvokeContext;
import com.tny.game.net.message.Message;
import com.tny.game.net.session.Session;
import com.tny.game.net.tunnel.Tunnel;
import com.tny.game.net.utils.AppConstants;
import com.tny.game.suite.login.IDAide;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.tny.game.suite.SuiteProfiles.*;

@Component
@Profile({SCHEDULER, GAME})
public class TaskReceiverSchedulerPlugin implements ControllerPlugin<Long> {

    public static final Logger LOGGER = LoggerFactory.getLogger(TaskReceiverSchedulerPlugin.class);

    public static final Logger TEST_LOGGER = LoggerFactory.getLogger("test");

    @Resource
    private TimeTaskSchedulerService taskSchedulerService;


    @Override
    public void execute(Tunnel<Long> tunnel, Message<Long> message, InvokeContext context) throws Exception {
        if (tunnel.getUserGroup().equals(AppConstants.DEFAULT_USER_GROUP)) {
            if (IDAide.isSystem(message.getUserID())) {
                Session<Long> session = tunnel.getSession();
                TEST_LOGGER.error("{} 非玩家ID | 登陆 {} | session {} | 请求 {} 协议", message.getUserID(), message.isLogin(), session, message.getProtocol(), new RuntimeException());
            } else {
                try {
                    this.taskSchedulerService.checkPlayerTask(message.getUserID(), ReceiverType.PLAYER);
                } catch (Exception e) {
                    LOGGER.error("", e);
                }
            }
        }
    }

}

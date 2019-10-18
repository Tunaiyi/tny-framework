package com.tny.game.suite.scheduler;

import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.plugins.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import com.tny.game.suite.login.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.tny.game.suite.SuiteProfiles.*;

@Component
@Profile({SCHEDULER, GAME})
public class TaskReceiverSchedulerPlugin implements VoidCommandPlugin<Long> {

    public static final Logger LOGGER = LoggerFactory.getLogger(TaskReceiverSchedulerPlugin.class);

    public static final Logger TEST_LOGGER = LoggerFactory.getLogger("test");

    @Resource
    private TimeTaskSchedulerService taskSchedulerService;

    @Override
    public void doExecute(Tunnel<Long> tunnel, Message<Long> message, CommandContext context) throws Exception {
        if (tunnel.getUserType().equals(Certificates.DEFAULT_USER_TYPE)) {
            if (IDAide.isSystem(message.getUserId())) {
                TEST_LOGGER.error("{} 非玩家ID | 登陆 {} | tunnel {} | 请求 {} 协议", message.getUserId(), message.isLogin(), tunnel, message.getProtocol(), new RuntimeException());
            } else {
                try {
                    this.taskSchedulerService.checkPlayerTask(message.getUserId(), ReceiverType.PLAYER);
                } catch (Exception e) {
                    LOGGER.error("", e);
                }
            }
        }
    }
}

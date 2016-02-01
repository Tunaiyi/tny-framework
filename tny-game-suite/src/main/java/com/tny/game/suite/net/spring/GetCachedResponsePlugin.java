package com.tny.game.suite.net.spring;

import com.tny.game.net.base.CoreResponseCode;
import com.tny.game.net.base.ResultFactory;
import com.tny.game.net.dispatcher.CommandResult;
import com.tny.game.net.dispatcher.Request;
import com.tny.game.net.dispatcher.Session;
import com.tny.game.net.dispatcher.plugin.ControllerPlugin;
import com.tny.game.net.dispatcher.plugin.PluginContext;
import com.tny.game.net.dispatcher.session.mobile.MobileAttach;
import com.tny.game.net.dispatcher.session.mobile.MobileSessionHolder;
import com.tny.game.net.dispatcher.session.mobile.ResponseItem;
import com.tny.game.suite.core.SessionKeys;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"suite.server", "suite.all"})
public class GetCachedResponsePlugin implements ControllerPlugin {


    @Override
    public CommandResult execute(Request request, CommandResult result, PluginContext context) throws Exception {
        if (request.getUserGroup() == Session.DEFAULT_USER_GROUP) {
            if (request.isHasAttributes() && request.attributes().getAttribute(SessionKeys.GET_CACHED_RESPONSE, false)) {
                MobileAttach attach = request.getSession().attributes().getAttribute(MobileSessionHolder.MOBILE_ATTACH);
                if (attach != null) {
                    ResponseItem item = attach.get(request.getID());
                    if (item != null)
                        return ResultFactory.create(item.getResultCode(), item.getBody());
                }
                return ResultFactory.fail(CoreResponseCode.RESPONSE_TIMEOUT);
            }
        }
        return context.passToNext(request, result);
    }
}

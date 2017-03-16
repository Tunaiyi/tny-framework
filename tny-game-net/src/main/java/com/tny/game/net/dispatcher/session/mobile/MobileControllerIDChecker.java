package com.tny.game.net.dispatcher.session.mobile;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.tny.game.common.result.ResultCode;
import com.tny.game.net.base.CoreResponseCode;
import com.tny.game.net.checker.ControllerChecker;
import com.tny.game.net.dispatcher.Request;
import com.tny.game.net.session.Session;

import java.util.List;
import java.util.Set;

/**
 * Created by Kun Yang on 16/7/11.
 */
public class MobileControllerIDChecker implements ControllerChecker {

    private List<Integer> directProtocols = ImmutableList.of();

    private Set<String> checkGroups = ImmutableSet.of();

    public MobileControllerIDChecker(List<Integer> directProtocols, Set<String> checkGroups) {
        if (directProtocols != null)
            this.directProtocols = directProtocols;
        this.checkGroups = ImmutableSet.copyOf(checkGroups);
    }

    @Override
    public boolean isCheck(Request request) {
        return checkGroups.isEmpty() || checkGroups.contains(request.getUserGroup());
    }

    @Override
    public ResultCode match(Request request) {
        if (directProtocols.contains(request.getProtocol()))
            return ResultCode.SUCCESS;
        Session session = request.getSession();
        MobileAttach attach = session.attributes().getAttribute(MobileSessionHolder.MOBILE_ATTACH);
        return attach.checkAndUpdate(request) ? ResultCode.SUCCESS : CoreResponseCode.REQUEST_TIMEOUT;
    }

}

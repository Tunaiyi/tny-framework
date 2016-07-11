package com.tny.game.net.dispatcher.session.mobile;

import com.google.common.collect.ImmutableList;
import com.tny.game.common.result.ResultCode;
import com.tny.game.net.base.CoreResponseCode;
import com.tny.game.net.checker.RequestChecker;
import com.tny.game.net.dispatcher.Request;
import com.tny.game.net.dispatcher.Session;

import java.util.List;

/**
 * Created by Kun Yang on 16/7/11.
 */
public class MobileRequestIDChecker implements RequestChecker {

    private List<Integer> directProtocols = ImmutableList.of();

    public MobileRequestIDChecker(List<Integer> directProtocols) {
        if (directProtocols != null)
            this.directProtocols = directProtocols;
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

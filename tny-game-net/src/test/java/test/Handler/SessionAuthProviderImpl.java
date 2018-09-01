package test.Handler;

import com.tny.game.net.command.auth.AuthProvider;
import com.tny.game.net.exception.DispatchException;
import com.tny.game.net.message.Message;
import com.tny.game.net.session.NetCertificate;
import com.tny.game.net.tunnel.Tunnel;

public final class SessionAuthProviderImpl<UID> implements AuthProvider<UID> {

    @Override
    public NetCertificate<UID> validate(Tunnel<UID> tunnel, Message<UID> message) throws DispatchException {
        // int id = message.getParameter(0, Integer.class);
        // if (id == 1L || message.size() != 4)
        //     return LoginCertificate.createUnLogin();
        //		String group = request.getParameter(2, String.class);
        //		String ip = request.getParameter(3, String.class);
        //		ServerContext context = factory.getServerContext();
        //		SystemIp systemIp = context.getSystemIpByName(group);
        //		if (systemIp == null)
        //			return LoginInfo.createUserLogin(id);
        //		if (systemIp.isInclude(ip))
        //			return LoginInfo.createSystemorLogin(-1, group);
        return NetCertificate.createUnLogin();
    }

}

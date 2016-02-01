package test.Handler;

import com.tny.game.net.LoginCertificate;
import com.tny.game.net.dispatcher.AuthProvider;
import com.tny.game.net.dispatcher.Request;

public final class SessionAuthProviderImpl implements AuthProvider {

    @Override
    public String getName() {
        return "";
    }

    @Override
    public LoginCertificate validate(final Request request) {
        int id = request.getParameter(0, Integer.class);
        if (id == 1L || request.size() != 4)
            return LoginCertificate.createUnLogin();
        //		String group = request.getParameter(2, String.class);
        //		String ip = request.getParameter(3, String.class);
        //		ServerContext context = factory.getServerContext();
        //		SystemIp systemIp = context.getSystemIpByName(group);
        //		if (systemIp == null)
        //			return LoginInfo.createUserLogin(id);
        //		if (systemIp.isInclude(ip))
        //			return LoginInfo.createSystemorLogin(-1, group);
        return LoginCertificate.createUnLogin();
    }

    @Override
    public boolean isCanValidate(Request request) {
        return true;
    }

}

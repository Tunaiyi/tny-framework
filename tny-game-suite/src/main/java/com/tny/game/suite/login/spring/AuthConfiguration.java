package com.tny.game.suite.login.spring;

import com.tny.game.common.ExceptionUtils;
import com.tny.game.net.dispatcher.AuthProvider;
import com.tny.game.suite.login.ServeAuthProvider;
import com.tny.game.suite.login.UserLoginAuthProvider;
import com.tny.game.suite.login.UserReloginAuthProvider;
import com.tny.game.suite.utils.Configs;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;

/**
 * Game Suite 的默认配置
 * Created by Kun Yang on 16/1/27.
 */
@Configuration
public class AuthConfiguration {

    @Bean
    @Profile({"suite.game_auth", "suite.all"})
    public AuthProvider userLoginAuthProvider() {
        return new UserLoginAuthProvider(getAuthProtocols(Configs.SUITE_AUTH_USER_LOGIN_PROTOCOLS));
    }

    @Bean
    @Profile({"suite.game_auth", "suite.all"})
    public AuthProvider userReloginAuthProvider() {
        return new UserReloginAuthProvider(getAuthProtocols(Configs.SUITE_AUTH_USER_RELOGIN_PROTOCOLS));
    }

    @Bean
    @Profile({"suite.serve_auth", "suite.all"})
    public AuthProvider serveAuthProvider() {
        return new ServeAuthProvider(getAuthProtocols(Configs.SUITE_AUTH_SERV_LOGIN_PROTOCOLS));
    }

    private List<Integer> getAuthProtocols(String key) {
        List<Integer> prots = new ArrayList<>();
        String protsWord = Configs.SUITE_CONFIG.getStr(key);
        ExceptionUtils.checkNotNull(protsWord);
        if (protsWord != null) {
            String[] ps = StringUtils.split(protsWord, ",");
            for (String port : StringUtils.split(protsWord, ","))
                prots.add(NumberUtils.toInt(port));
        }
        return prots;
    }

}

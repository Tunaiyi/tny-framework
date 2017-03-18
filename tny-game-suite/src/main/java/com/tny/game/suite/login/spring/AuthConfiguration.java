package com.tny.game.suite.login.spring;

import com.tny.game.common.ExceptionUtils;
import com.tny.game.net.auth.AuthProvider;
import com.tny.game.suite.login.ServeAuthProvider;
import com.tny.game.suite.login.UserLoginAuthProvider;
import com.tny.game.suite.login.UserReloginAuthProvider;
import com.tny.game.suite.utils.Configs;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.HashSet;
import java.util.Set;

import static com.tny.game.suite.SuiteProfiles.*;

/**
 * Game Suite 的默认配置
 * Created by Kun Yang on 16/1/27.
 */
@Configuration
public class AuthConfiguration {

    @Bean
    @Profile({GAME})
    public AuthProvider userLoginAuthProvider() {
        return new UserLoginAuthProvider(getAuthProtocols(Configs.SUITE_AUTH_USER_LOGIN_PROTOCOLS));
    }

    @Bean
    @Profile({GAME})
    public AuthProvider userReloginAuthProvider() {
        return new UserReloginAuthProvider(getAuthProtocols(Configs.SUITE_AUTH_USER_RELOGIN_PROTOCOLS));
    }

    @Bean
    @Profile({GAME, SERVER_AUTH})
    public AuthProvider serveAuthProvider() {
        return new ServeAuthProvider(getAuthProtocols(Configs.SUITE_AUTH_SERV_LOGIN_PROTOCOLS));
    }

    private Set<Integer> getAuthProtocols(String key) {
        Set<Integer> prots = new HashSet<>();
        String protsWord = Configs.SUITE_CONFIG.getStr(key);
        ExceptionUtils.checkNotNull(protsWord);
        for (String port : StringUtils.split(protsWord, ","))
            prots.add(NumberUtils.toInt(port));
        return prots;
    }

}
